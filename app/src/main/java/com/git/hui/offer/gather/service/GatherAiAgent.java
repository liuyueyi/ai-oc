package com.git.hui.offer.gather.service;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.gather.service.helper.GatherResFormat;
import com.git.hui.offer.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AI采集代理
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@Component
public class GatherAiAgent {
    private static final String SYSTEM_PROMPT = """
            你现在是一个专业的数据挖掘者，可以从我提供给你的文本内容、表格文件、html文本中获取用户希望的信息；
            如果我给你的是一个http链接，则借助function tool crawlerHttpTable从链接对应的网页中找到表格元素返回给用户希望的信息
             """;

    private final ChatClient chatClient;

    private final ChatModel chatModel;

    private BeanOutputConverter<ArrayList<GatherOcDraftBo>> gatherResConverter;

    @Autowired
    public GatherAiAgent(ZhiPuAiChatModel chatModel) {
        this.chatModel = chatModel;
        chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder().stopSequences(Collections.emptyList()).build()) // 取消默认停止符
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        gatherResConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<ArrayList<GatherOcDraftBo>>() {
        });
    }


    // fixme 需要处理传入数据太长，导致解析的结果被截断的场景
    public List<GatherOcDraftBo> gatherByText(String text) {
        ArrayList<GatherOcDraftBo> list = chatClient.prompt(text)
                .tools(new CrawlerTools())
                .call()
                .entity(new ParameterizedTypeReference<ArrayList<GatherOcDraftBo>>() {
                });
        return list;
    }


    public List<GatherOcDraftBo> gatherByAutoSplit(String text) {
        // 为了避免响应过长，这里进行分段处理
        // 创建 memory 实例，保存上下文
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        String conversationId = RandomUtil.randomString(6);

        SystemMessage systemMessage = new SystemMessage(SYSTEM_PROMPT);
        chatMemory.add(conversationId, systemMessage);

        List<String> itemList = new ArrayList<>();
        StringBuilder remain = new StringBuilder();
        int cnt = 0;
        while (true) {
            log.info("第{}次大模型数据解析", cnt + 1);
            UserMessage msg;
            if (cnt == 0) {
                msg = new UserMessage(new PromptTemplate("{text}.{format}").render(Map.of("text", text, "format", gatherResConverter.getFormat())));
            } else {
                msg = new UserMessage("你之前返回的结果不完整，继续返回剩余的内容");
            }
            chatMemory.add(conversationId, msg);

            // 工具
            ChatOptions chatOptions = ToolCallingChatOptions.builder()
                    .toolCallbacks(ToolCallbacks.from(new CrawlerTools()))
                    .build();
            try {
                ChatResponse response = chatModel.call(new Prompt(chatMemory.get(conversationId), chatOptions));
                AssistantMessage assistantMessage = response.getResult().getOutput();
                chatMemory.add(conversationId, assistantMessage);
                cnt += 1;

                String outText = assistantMessage.getText().trim();
                itemList.addAll(GatherResFormat.extact(remain, outText));
//                list.addAll(GatherResFormat.discardBrokenGatherItem(gatherResConverter, outText));
                if (cnt > 1 && outText.startsWith("```json")) {
                    // 表示大模型总是返回相同的数据，直接跳出循环
                    break;
                }
                if (outText.endsWith("```") || cnt >= 10) {
                    // 做一个次数限制，避免无效调用大模型
                    break;
                }
            } catch (Exception e) {
                // 避免因为多次调用模型出现异常，导致前面获取的数据被丢掉，我们直接跳出来，将已经解析的结果保存下来
                log.error("gather error: {}", e.getMessage());
                break;
            }
        }

        List<GatherOcDraftBo> list = new ArrayList<>();
        if (!itemList.isEmpty()) {
            // 非空时，尝试合并为一个大的json数组字符串
            StringBuilder toParse = new StringBuilder("[");
            for (String item : itemList) {
                try {
                    list.add(JsonUtil.toObj(item, GatherOcDraftBo.class));
                } catch (Exception e) {
                    log.warn("解析异常: {}", item, e);
                }
            }
        }

        return list;
    }

    public class CrawlerTools {
        /**
         * 获取http地址中的表格
         *
         * @param url
         * @return
         */
        @Tool(description = "从给定的http对应的网页中获取表格内容")
        public String crawlerHttpTable(@ToolParam(description = "url地址") String url) {
            log.info("开始获取表格内容: {}", url);
            String text = HttpUtil.get(url, CharsetUtil.CHARSET_UTF_8);
            Document document = Jsoup.parse(text);
            Element table = document.select("table").first();
            String ans = table.html().trim();
            if (log.isDebugEnabled()) {
                log.debug("获取到的表格内容为：{}", ans);
            }
            return ans;
        }
    }

}

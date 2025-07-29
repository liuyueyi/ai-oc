package com.git.hui.offer.gather.service;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.gather.service.ai.AiModelFacade;
import com.git.hui.offer.gather.service.helper.GatherResFormat;
import com.git.hui.offer.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI采集代理
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@Component
public class GatherAiAgent {
    private final AiModelFacade aiModelFacade;
    private BeanOutputConverter<ArrayList<GatherOcDraftBo>> gatherResConverter;

    @Autowired
    public GatherAiAgent(AiModelFacade aiModelFacade) {
        this.aiModelFacade = aiModelFacade;
        this.gatherResConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {
        });
    }


    // 传入数据太长，导致解析的结果被截断的场景时，转用下面的 gatherByAutoSplit 调用方法
    public List<GatherOcDraftBo> gatherByText(String text) {
        ArrayList<GatherOcDraftBo> list = this.aiModelFacade.getChatClient().prompt(text)
                .tools(new CrawlerTools())
                .call()
                .entity(new ParameterizedTypeReference<ArrayList<GatherOcDraftBo>>() {
                });
        return list;
    }

    // 适用于图片中的数据条目较小的场景，大模型可以一次将结果全部返回
    public List<GatherOcDraftBo> gatherByImg(MimeType type, byte[] bytes) {
        String rid = UUID.randomUUID().toString();
        Media media = Media.builder().mimeType(type)
                .data(bytes)
                .name("图片-" + type.getSubtype() + "-" + rid)
                .id("")
                .build();
        UserMessage msg = UserMessage.builder()
                .media(media)
                .text("提取图片中的表格信息，按照指定要求返回")
                .build();
        ArrayList<GatherOcDraftBo> list = this.aiModelFacade.getImgChatClient().prompt(new Prompt(msg))
                .tools(new CrawlerTools())
                .call()
                .entity(new ParameterizedTypeReference<ArrayList<GatherOcDraftBo>>() {
                });
        return list;
    }

    /**
     * fixme 说明：智谱的几个免费大模型，不支持文件上传解析；若是其他的模型则可以考虑使用这个方式
     *
     * @param type
     * @param bytes
     * @return
     */
    public List<GatherOcDraftBo> gatherByFile(MimeType type, byte[] bytes) {
        String rid = UUID.randomUUID().toString();
        Media media = Media.builder().mimeType(type)
                .data(bytes)
                .name("文件" + type.getSubtype() + "-" + rid)
                .id(rid)
                .build();
        UserMessage msg = UserMessage.builder()
                .media(media)
                .text("读取给你的文件，按照指定要求返回")
                .build();
        ArrayList<GatherOcDraftBo> list = this.aiModelFacade.getChatClient().prompt(new Prompt(msg))
                .tools(new CrawlerTools())
                .call()
                .entity(new ParameterizedTypeReference<ArrayList<GatherOcDraftBo>>() {
                });
        return list;
    }

    /**
     * 基于文本/http链向的网页进行数据提取
     *
     * @param text
     * @return
     */
    public List<GatherOcDraftBo> gatherByAutoSplit(String text) {
        return autoContinueChat(null, text);
    }


    /**
     * 适用于图片内容较多，返回结果被截断的场景
     *
     * @param type
     * @param bytes
     * @return
     */
    public List<GatherOcDraftBo> gatherByImgAutoSplit(MimeType type, byte[] bytes) {
        String rid = UUID.randomUUID().toString();
        Media media = Media.builder().mimeType(type)
                .data(bytes)
                .name("图片-" + type.getSubtype() + "-" + rid)
                .id("")
                .build();
        return autoContinueChat(media, "提取图片中的表格信息，按照指定要求返回");
    }

    /**
     * 针对大模型响应结果截断的场景，进行多轮对话，尝试获取完整的返回
     * 实现原理：基于 chatModel, 借助 ChatMemory 自动实现多轮对话，
     */
    private List<GatherOcDraftBo> autoContinueChat(Media media, String text) {
        // 创建 memory 实例，保存上下文
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        String chatId = RandomUtil.randomString(6);

        SystemMessage systemMessage = new SystemMessage(AiModelFacade.SYSTEM_PROMPT);
        chatMemory.add(chatId, systemMessage);

        List<String> itemList = new ArrayList<>();
        StringBuilder remain = new StringBuilder();
        int cnt = 0;
        while (true) {
            log.info("{}#第{}次大模型数据解析", chatId, cnt + 1);
            UserMessage msg;
            if (cnt == 0) {
                UserMessage.Builder builder = UserMessage.builder()
                        .text(new PromptTemplate("{text}.{format}")
                                .render(Map.of("text", text, "format", gatherResConverter.getFormat()))
                        );
                if (media != null) {
                    builder.media(media);
                }
                msg = builder.build();
            } else {
                msg = new UserMessage("你之前返回的结果不完整，继续返回剩余的内容");
            }
            chatMemory.add(chatId, msg);

            // 工具
            ChatOptions chatOptions = ToolCallingChatOptions.builder()
                    .model(aiModelFacade.getModel(media))
                    // 注册给大模型回调的工具
                    .toolCallbacks(ToolCallbacks.from(new CrawlerTools()))
                    .build();
            try {
                Prompt query = new Prompt(chatMemory.get(chatId), chatOptions);
                if (log.isDebugEnabled()) {
                    // 一行显示日志
                    log.debug("{}#req: {}", chatId, StringUtils.replaceChars(query.toString(), "\n", ""));
                }
                ChatResponse response = aiModelFacade.getChatModel().call(query);
                AssistantMessage assistantMessage = response.getResult().getOutput();
                if (log.isDebugEnabled()) {
                    // 一行显示和日志
                    log.debug("{}#res: {}", chatId, StringUtils.replaceChars(assistantMessage.toString(), "\n", ""));
                }

                chatMemory.add(chatId, assistantMessage);
                cnt += 1;

                String outText = assistantMessage.getText().trim();
                itemList.addAll(GatherResFormat.extact(remain, outText));
                if (outText.endsWith("```") || cnt >= 10) {
                    // 做一个次数限制，避免死循环的调用大模型
                    log.info("{}#经过{}论对话，完成大模型调用", chatId, cnt);
                    break;
                }
                if (cnt > 1 && outText.startsWith("```json")) {
                    int jsonBeginIndex = outText.indexOf("[");
                    if (jsonBeginIndex > 0 && jsonBeginIndex < 15) {
                        // 表示大模型又重新返回了完整的数据，为了避免大模型总是重复解析，我们直接退出循环
                        log.info("{}#大模型重复返回完整解析数据，主动退出多轮对话", chatId);
                        break;
                    }
                }
            } catch (Exception e) {
                // 避免因为多次调用模型出现异常，导致前面获取的数据被丢掉，我们直接跳出来，将已经解析的结果保存下来
                log.error("{}#gather error", chatId, e);
                break;
            }
        }

        if (itemList.isEmpty()) {
            return List.of();
        }

        return itemList.stream().map(this::toBo).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private GatherOcDraftBo toBo(String item) {
        try {
            return JsonUtil.toObj(item, GatherOcDraftBo.class);
        } catch (Exception e) {
            log.warn("解析异常: {}", item, e);
        }
        return null;
    }


    /**
     * 提供给大模型的 function tools
     */
    public class CrawlerTools {
        /**
         * 获取http地址中的表格
         * <p>
         * 说明：即便我给大模型的是一个http链接，但是无法保证大模型每次都会触发调用这个方法(😂)
         *
         * @param url
         * @return
         */
        @Tool(description = "输入一个http链接，返回这个http链接对应的网页中的表格内容")
        public String crawlerHttpTable(@ToolParam(description = "http格式的url地址") String url) {
            log.info("开始获取表格内容: {}", url);
            String text = HttpUtil.get(url, CharsetUtil.CHARSET_UTF_8);
            Document document = Jsoup.parse(text);
            Element table = document.select("table").first();
            String ans = table.html().trim();
            if (log.isDebugEnabled()) {
                // 一行打印
                log.debug("获取到的表格内容为：{}", ans.replaceAll("\n", ""));
            }
            return ans;
        }

        @Tool(description = "将给入的文件内容转换为文本返回")
        public String readFileContent(@ToolParam(description = "文件路径") byte[] bytes) {
            log.info("将给入的数据转换为文本返回");
            return new String(bytes);
        }
    }

}

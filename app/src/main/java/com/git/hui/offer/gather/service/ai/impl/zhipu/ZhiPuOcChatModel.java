package com.git.hui.offer.gather.service.ai.impl.zhipu;

import com.git.hui.offer.constants.gather.GatherModelEnum;
import com.git.hui.offer.gather.service.ai.impl.AbsOcChatModelApi;
import io.modelcontextprotocol.client.McpAsyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/30
 */
@Component
public class ZhiPuOcChatModel extends AbsOcChatModelApi {
    // 智谱的图片模型
    private static final String IMG_MODEL = "GLM-4V-Flash";
    // 智谱的文本模型
    private static final String CHAT_MODEL = "GLM-4-Flash";

    private final ZhiPuAiChatModel zhiPuAiChatModel;

    private final ChatClient chatClient;
    private final ChatClient imgClient;

    public ZhiPuOcChatModel(ZhiPuAiChatModel zhiPuAiChatModel, List<McpAsyncClient> mcpClients) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;

        chatClient = ChatClient.builder(zhiPuAiChatModel)
                .defaultSystem(GATHER_SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder().stopSequences(Collections.emptyList()).build()) // 取消默认停止符
                .defaultAdvisors(new SimpleLoggerAdvisor())
                // 将MCP Client 注册为工具
                .defaultToolCallbacks(new AsyncMcpToolCallbackProvider(mcpClients))
                .build();

        // 图片理解
        imgClient = ChatClient.builder(zhiPuAiChatModel)
                .defaultSystem(GATHER_SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder()
                        .model(IMG_MODEL)
                        .stopSequences(Collections.emptyList()).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    @Override
    public GatherModelEnum modelEnum() {
        return GatherModelEnum.ZHIPU;
    }

    @Override
    public ChatClient chatClient() {
        return chatClient;
    }

    @Override
    public ChatClient imgClient() {
        return imgClient;
    }

    @Override
    public ChatModel chatModel() {
        return zhiPuAiChatModel;
    }

    @Override
    public String chatModelName() {
        return CHAT_MODEL;
    }

    @Override
    public String imgModelName() {
        return IMG_MODEL;
    }
}

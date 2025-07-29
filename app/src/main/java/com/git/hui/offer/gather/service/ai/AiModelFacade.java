package com.git.hui.offer.gather.service.ai;

import com.git.hui.offer.gather.service.ai.impl.spark.SparkLiteModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author YiHui
 * @date 2025/7/18
 */
@Component
public class AiModelFacade {
    public static final String SYSTEM_PROMPT = """
            你现在是一个专业的数据挖掘者，可以从我提供给你的文本内容、表格文件、html文本中获取用户希望的信息；
            如果我给你的是一个http链接，则借助function tool crawlerHttpTable从链接对应的网页中找到表格元素返回给用户希望的信息
             """;
    private static final String IMG_MODEL = "GLM-4V-Flash";
    private static final String CHAT_MODEL = "GLM-4-Flash";

    /**
     * 文本类大模型
     */
    private final ChatClient chatClient;

    /**
     * 图片视觉理解的模型
     */
    private final ChatClient imgChatClient;

    private final ChatModel chatModel;

    private final SparkLiteModel sparkLiteModel;

    /**
     * fixme 待集成多个模型
     *
     * @param chatModel
     */
    public AiModelFacade(ZhiPuAiChatModel chatModel, SparkLiteModel sparkLiteModel) {
        this.chatModel = chatModel;
        this.sparkLiteModel = sparkLiteModel;

        chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder().stopSequences(Collections.emptyList()).build()) // 取消默认停止符
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        // 图片理解
        imgChatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder()
                        .model(IMG_MODEL)
                        .stopSequences(Collections.emptyList()).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public ChatClient getImgChatClient() {
        return imgChatClient;
    }


    public ChatModel getChatModel() {
        return chatModel;
    }

    public ChatClient getSparkClient() {
        return ChatClient.builder(sparkLiteModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultOptions(ChatOptions.builder().stopSequences(Collections.emptyList()).build()) // 取消默认停止符
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public String getModel(Media media) {
        return media == null ? CHAT_MODEL : IMG_MODEL;
    }
}

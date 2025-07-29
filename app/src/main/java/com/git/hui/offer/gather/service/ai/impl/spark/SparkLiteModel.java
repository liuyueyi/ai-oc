package com.git.hui.offer.gather.service.ai.impl.spark;

import com.git.hui.offer.gather.service.ai.impl.spark.config.SparkConfig;
import com.git.hui.offer.gather.service.ai.impl.spark.pojo.SparkModelConvert;
import com.git.hui.offer.gather.service.ai.impl.spark.pojo.SparkPOJO;
import com.git.hui.offer.util.json.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Consumer;

/**
 * 一个简单的，基于星火 Spark Lite实现大模型交互实现
 * <p>
 * <a href="https://www.xfyun.cn/doc/spark/HTTP%E8%B0%83%E7%94%A8%E6%96%87%E6%A1%A3.html">官方调用文档</a>
 *
 * @author YiHui
 * @date 2025/7/21
 */
@Slf4j
@Component
public class SparkLiteModel implements ChatModel {
    private RestClient restClient;
    @Autowired
    private final SparkConfig sparkConfig;

    public SparkLiteModel(SparkConfig sparkConfig) {
        this.sparkConfig = sparkConfig;
    }

    @PostConstruct
    public void init() {
        Consumer<HttpHeaders> authHeaders = (h) -> {
            h.setBearerAuth(sparkConfig.getApiKey());
            h.setContentType(MediaType.APPLICATION_JSON);
        };

        this.restClient = RestClient.builder().baseUrl(sparkConfig.getBaseUrl()).defaultHeaders(authHeaders).build();
    }

    /**
     * 配置默认的查询条件
     *
     * @return
     */
    @Override
    public ChatOptions getDefaultOptions() {
        return ChatOptions.builder()
                .model(sparkConfig.getChat().options().model())
                .build();
    }

    /**
     * 这里实现了一个基本的模型调用逻辑
     *
     * @param prompt
     * @return
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        Long reqTime = System.currentTimeMillis();
        String model = (prompt.getOptions() == null || prompt.getOptions().getModel() == null) ? sparkConfig.getChat().options().model() : prompt.getOptions().getModel();
        String res = restClient.post()
                .body(SparkModelConvert.toReq(prompt, model))
                .retrieve()
                .body(String.class);

        SparkPOJO.ChatCompletionChunk chatCompletionChunk = JsonUtil.toObj(res, SparkPOJO.ChatCompletionChunk.class);
        List<Generation> generations = SparkModelConvert.generationList(chatCompletionChunk);
        ChatResponse response = new ChatResponse(generations, SparkModelConvert.from(reqTime, model, chatCompletionChunk));
        return response;
    }
}

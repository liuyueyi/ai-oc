package com.git.hui.offer.gather.service;

import com.git.hui.offer.gather.model.GatherOcDraftBo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * AI采集代理
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Component
public class GatherAiAgent {

    private final ChatClient chatClient;

    @Autowired
    public GatherAiAgent(ZhiPuAiChatModel chatModel) {
        chatClient = ChatClient.builder(chatModel)
                .defaultSystem("你现在是一个专业的数据挖掘者，可以从我提供给你的文本内容、网页地址、表格文件、html文本中获取用户希望的信息")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    public List<GatherOcDraftBo> gatherByText(String text) {
        ArrayList<GatherOcDraftBo> list = chatClient.prompt(text).call().entity(new ParameterizedTypeReference<ArrayList<GatherOcDraftBo>>() {
        });
        return list;
    }
}

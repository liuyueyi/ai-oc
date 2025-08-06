package com.git.hui.offer.gather.service.ai;

import com.git.hui.offer.constants.gather.GatherModelEnum;
import com.git.hui.offer.gather.model.ModelSelectReq;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.util.Pair;

/**
 * 校招派大模型集成的统一接口
 *
 * @author YiHui
 * @date 2025/7/30
 */
public interface OcChatModelApi {
    String GATHER_SYSTEM_PROMPT = """
            你现在是一个专业的数据挖掘者，可以从我提供给你的文本内容、表格文件、html文本中获取用户希望的信息；
            如果我给你的是一个http链接，则借助function tool从链接对应的网页中找到表格元素返回给用户希望的信息
             """;

    /**
     * 根据传参获取对应de
     *
     * @param req
     * @return
     */
    ChatClient chatClient(ModelSelectReq req);

    /**
     * 获取模型
     *
     * @return left: 大模型  right: 模型名
     */
    Pair<ChatModel, String> model(ModelSelectReq req);

    /**
     * 当前模型对应的枚举定义，用于模型选择切换
     *
     * @return
     */
    GatherModelEnum modelEnum();
}

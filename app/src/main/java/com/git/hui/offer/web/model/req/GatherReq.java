package com.git.hui.offer.web.model.req;

import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;

/**
 * 请求实体
 *
 * @author YiHui
 * @date 2025/7/14
 */
public record GatherReq(
        // 传入的内容
        String content
        // 传入的内容类型
        , Integer type
        // 指定的模型
        , String model
) {
}

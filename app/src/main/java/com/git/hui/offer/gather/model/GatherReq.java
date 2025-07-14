package com.git.hui.offer.gather.model;

import com.git.hui.offer.model.gather.GatherTargetTypeEnum;

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
        , GatherTargetTypeEnum type
        // 指定的模型
        , String model
) {
}

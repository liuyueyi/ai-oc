package com.git.hui.offer.gather.model;

import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;

/**
 * @author YiHui
 * @date 2025/7/18
 */
public record GatherTaskProcessBo(Long taskId, GatherTargetTypeEnum type, String model, String content) {
}

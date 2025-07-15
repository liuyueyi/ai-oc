package com.git.hui.offer.gather.model;

import java.util.List;

/**
 * 采集到的oc草稿数据
 *
 * @author YiHui
 * @date 2025/7/14
 */
public record ListGatherOcDraftBo(
        Integer size,
        List<GatherOcDraftBo> list
) {
}

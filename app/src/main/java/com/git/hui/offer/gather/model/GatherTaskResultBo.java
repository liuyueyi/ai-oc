package com.git.hui.offer.gather.model;

import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/18
 */
public record GatherTaskResultBo(String msg, List<Long> insertDraftIds, List<Long> updateDraftIds) {
    public static final String SUCCESS = "success";

    public boolean isSuccess() {
        return SUCCESS.equals(msg);
    }
}
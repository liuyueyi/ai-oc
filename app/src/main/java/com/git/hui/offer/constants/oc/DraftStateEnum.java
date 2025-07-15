package com.git.hui.offer.constants.oc;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum DraftStateEnum implements IntBaseEnum {
    DELETED(-1, "已删除"),
    DRAFT(0, "草稿中"),
    PUBLISHED(1, "已发布");

    private final Integer value;
    private final String desc;

    DraftStateEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

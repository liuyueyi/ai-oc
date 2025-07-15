package com.git.hui.offer.constants.oc;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum DraftProcessEnum implements IntBaseEnum {
    UNPROCESS(0, "未处理"),
    PROCEED(1, "已处理"),;

    private final Integer value;
    private final String desc;

    DraftProcessEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

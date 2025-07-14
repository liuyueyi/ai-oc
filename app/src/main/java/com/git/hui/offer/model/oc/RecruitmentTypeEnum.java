package com.git.hui.offer.model.oc;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * 招聘类型
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum RecruitmentTypeEnum implements IntBaseEnum {
    ,;

    private final Integer value;
    private final String desc;
    RecruitmentTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

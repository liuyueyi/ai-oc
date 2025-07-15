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
    SOCIAL_RECRUITMENT(0, "社招"),
    SPRING_RECRUITMENT(1, "春招"),
    AUTUMN_RECRUITMENT(2, "秋招"),
    SUPPLEMENTAL_ADMISSION(3, "补录"),
    EARLY_AUTUMN_RECRUITMENT(4, "春招提前批"),
    ;

    private final Integer value;
    private final String desc;

    RecruitmentTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

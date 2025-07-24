package com.git.hui.offer.constants.user;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * 充值层级
 *
 * @author YiHui
 * @date 2025/7/22
 */
@Getter
public enum RechargeLevelEnum implements IntBaseEnum {
    MONTH(0, "月卡会员", 31),
    QUARTER(1, "季卡会员", 93),
    YEAR(2, "年卡会员", 366),
    LIFE(3, "终身会员", 9999);
    private final Integer value;
    private final String desc;
    private final Integer days;

    RechargeLevelEnum(Integer value, String desc, Integer days) {
        this.value = value;
        this.desc = desc;
        this.days = days;
    }

    public Long getMillSeconds() {
        return days * 24 * 60 * 60 * 1000L;
    }
}

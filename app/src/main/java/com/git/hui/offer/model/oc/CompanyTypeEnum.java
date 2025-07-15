package com.git.hui.offer.model.oc;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * 公司类型
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum CompanyTypeEnum implements IntBaseEnum {
    STATE_ENTERPRISE(0, "央国企"),
    FOREIGN_ENTERPRISE(1, "外企"),
    PRIVATE_ENTERPRISE(2, "私企"),
    GOVERNMENT_AFFILIATED_INSTITUTIONS(3, "事业单位"),
    BANK(4, "银行"),
    SCHOOL(5, "学校"),
    ;

    private final Integer value;
    private final String desc;

    CompanyTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}

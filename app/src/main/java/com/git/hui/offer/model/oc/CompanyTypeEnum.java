package com.git.hui.offer.model.oc;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum CompanyTypeEnum implements IntBaseEnum {
    ,
    ;

    private final Integer value;
    private final String desc;

    CompanyTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}

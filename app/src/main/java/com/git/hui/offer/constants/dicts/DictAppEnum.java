package com.git.hui.offer.constants.dicts;

import com.git.hui.offer.util.json.StringBaseEnum;
import lombok.Getter;

/**
 * 字典的作用域
 *
 * @author YiHui
 * @date 2025/7/21
 */
@Getter
public enum DictAppEnum implements StringBaseEnum {
    /**
     * 公开的全局配置
     */
    SITE("site", "全局-站点配置"),
    /**
     * 适用于管理员在管理后台的相关配置
     */
    SERVER("server", "后台-服务配置"),
    /**
     * 单纯适用于服务器使用的配置
     */
    BIZ_DICTS("dicts", "业务-字典配置"),
    BIZ_GATHER("gather", "业务-数据录入"),
    BIZ_OC("oc", "业务-职位相关"),
    BIZ_USER("user", "业务-用户相关"),
    ;

    private String value;
    private String desc;

    DictAppEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

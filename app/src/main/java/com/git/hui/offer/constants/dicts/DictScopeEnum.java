package com.git.hui.offer.constants.dicts;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * 字典的作用域
 *
 * @author YiHui
 * @date 2025/7/21
 */
@Getter
public enum DictScopeEnum implements IntBaseEnum {
    /**
     * 公开的全局配置
     */
    PUBLIC_DICT(0, "公开配置"),
    /**
     * 适用于管理员在管理后台的相关配置
     */
    ADMIN_DICT(1, "管理配置"),
    /**
     * 单纯适用于服务器使用的配置
     */
    SERVER_DICT(2, "服务配置"),
    ;

    private Integer value;
    private String desc;

    DictScopeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

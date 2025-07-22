package com.git.hui.offer.constants.user.permission;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * @author YiHui
 * @date 2022/8/25
 */
@Getter
public enum UserRoleEnum implements IntBaseEnum {
    /**
     * 登录用户
     */
    NORMAL(1, "普通用户"),

    /**
     * 会员用户
     */
    VIP(2, "会员"),
    /**
     * 管理员
     */
    ADMIN(3, "管理员"),
    /**
     * 所有用户
     */
    ALL(0, "全部");

    private Integer value;
    private String desc;

    UserRoleEnum(int code, String msg) {
        this.value = code;
        this.desc = msg;
    }
}

package com.git.hui.offer.web.model;

import com.git.hui.offer.components.bizexception.StatusEnum;
import lombok.Data;

/**
 * 统一返回结果封装
 *
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class ResVo<T> {
    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MSG = "ok";
    private int code;
    private String msg;
    /**
     * 在线人数
     */
    private int online;
    private T data;

    public ResVo(T t) {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MSG;
        this.data = t;
    }

    public ResVo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResVo<T> success(T t) {
        return new ResVo<>(t);
    }

    public static <T> ResVo<T> fail(StatusEnum statusEnum) {
        return new ResVo<>(statusEnum.getCode(), statusEnum.getMsg());
    }

    public static <T> ResVo<T> fail(int code, String msg) {
        return new ResVo<>(code, msg);
    }

    public static <T> ResVo<T> fail(int code, String msg, T t) {
        ResVo<T> res = new ResVo<>(code, msg);
        res.data = t;
        return res;
    }


}

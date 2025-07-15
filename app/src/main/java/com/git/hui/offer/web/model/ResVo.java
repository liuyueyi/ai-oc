package com.git.hui.offer.web.model;

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

    public static <T> ResVo<T> fail(int code, String msg) {
        return new ResVo<>(code, msg);
    }
}

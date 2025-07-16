package com.git.hui.offer.components.bizexception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author YiHui
 * @date 2025/7/15
 */
@Getter
public class BizException extends RuntimeException {
    private int code;
    private String msg;

    public BizException(int code, String msg, Throwable cause) {
        super(cause);
        this.code = code;
        this.msg = msg;
    }

    public BizException(StatusEnum status, Object... args) {
        this.code = status.getCode();
        this.msg = (String.format(status.getMsg(), args));
    }
}

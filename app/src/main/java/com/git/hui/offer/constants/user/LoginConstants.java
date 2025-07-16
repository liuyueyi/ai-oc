package com.git.hui.offer.constants.user;

/**
 * @author YiHui
 * @date 2025/7/16
 */
public interface LoginConstants {
    String SESSION_KEY = "oc-session";
    String USER_DEVICE_KEY = "oc-device";

    /**
     * sse的超时时间，默认15min
     */
    Long SSE_EXPIRE_TIME = 15 * 60 * 1000L;
}

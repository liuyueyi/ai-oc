package com.git.hui.offer.user.helper;

import cn.hutool.core.codec.Base64Decoder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.git.hui.offer.util.json.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YiHui
 * @date 2025/7/16
 */
@Slf4j
@Component
public class SessionHelper {
    @Component
    @Data
    @ConfigurationProperties("oc.jwt")
    public static class JwtProperties {
        /**
         * 签发人
         */
        private String issuer;
        /**
         * 密钥
         */
        private String secret;
        /**
         * 有效期，毫秒时间戳
         */
        private Long expire;
    }

    private final JwtProperties jwtProperties;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    public SessionHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    public String genSession(Long userId) {
        // 1.生成jwt格式的会话，内部持有有效期，用户信息
        String session = JsonUtil.toStr(Map.of("uid", userId));
        String token = JWT.create().withIssuer(jwtProperties.getIssuer()).withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(session)
                .sign(algorithm);
        return token;
    }

    /**
     * 根据会话获取用户信息
     *
     * @param session
     * @return
     */
    public Long getUserIdBySession(String session) {
        // jwt的校验方式，如果token非法或者过期，则直接验签失败
        try {
            DecodedJWT decodedJWT = verifier.verify(session);
            String pay = Base64Decoder.decodeStr(decodedJWT.getPayload());
            // jwt验证通过，获取对应的userId
            String userId = String.valueOf(JsonUtil.toObj(pay, HashMap.class).get("uid"));
            return Long.valueOf(userId);
        } catch (Exception e) {
            log.info("jwt token校验失败! token: {}, msg: {}", session, e.getMessage());
            return null;
        }
    }
}

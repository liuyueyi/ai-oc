package com.git.hui.offer.user.service;

import com.git.hui.offer.components.bizexception.NoVlaInGuavaException;
import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.env.SpringUtil;
import com.git.hui.offer.constants.user.LoginConstants;
import com.git.hui.offer.user.helper.SessionHelper;
import com.git.hui.offer.util.CodeGenerateUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YiHui
 * @date 2022/9/5
 */
@Slf4j
@Service
public class LoginService {
    private final SessionHelper sessionHelper;

    /**
     * 对于单机的场景，可以直接使用本地局部变量来实现计数
     * 对于集群的场景，可考虑借助 redis的zset 来实现集群的在线用户人数统计
     */
    private AtomicInteger onlineUserCnt = new AtomicInteger(0);

    /**
     * key = 验证码, value = 长连接
     */
    private LoadingCache<String, SseEmitter> verifyCodeCache;
    /**
     * key = 设备 value = 验证码
     */
    private LoadingCache<String, String> deviceCodeCache;

    @Autowired
    public LoginService(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
        verifyCodeCache = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, SseEmitter>() {
            @Override
            public SseEmitter load(String s) throws Exception {
                throw new NoVlaInGuavaException("no val: " + s);
            }
        });

        deviceCodeCache = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
            @Override
            public String load(String s) {
                int cnt = 0;
                while (true) {
                    String code = CodeGenerateUtil.genCode(cnt++);
                    if (!verifyCodeCache.asMap().containsKey(code)) {
                        return code;
                    }
                }
            }
        });
    }

    /**
     * 保持与前端的长连接
     * <p>
     * 直接根据设备拿之前初始化的验证码，不直接使用传过来的code
     *
     * @return
     */
    public SseEmitter subscribe() throws IOException {
        String deviceId = ReqInfoContext.getReqInfo().getDeviceId();
        String realCode = deviceCodeCache.getUnchecked(deviceId);
        // fixme 设置15min的超时时间, 超时时间一旦设置不能修改；因此导致刷新验证码并不会增加连接的有效期
        SseEmitter sseEmitter = new SseEmitter(LoginConstants.SSE_EXPIRE_TIME);
        SseEmitter oldSse = verifyCodeCache.getIfPresent(realCode);
        if (oldSse != null) {
            oldSse.complete();
        }
        verifyCodeCache.put(realCode, sseEmitter);
        sseEmitter.onTimeout(() -> {
            log.info("sse 超时中断 --> {}", realCode);
            verifyCodeCache.invalidate(realCode);
            sseEmitter.complete();
        });
        sseEmitter.onError((e) -> {
            log.warn("sse error! --> {}", realCode, e);
            verifyCodeCache.invalidate(realCode);
            sseEmitter.complete();
        });
        // 若实际的验证码与前端显示的不同，则通知前端更新
        sseEmitter.send("initCode!");
        sseEmitter.send("qr#" + SpringUtil.getConfig("oc.site.login-qr-img"));
        sseEmitter.send("init#" + realCode);
        return sseEmitter;
    }

    public String resend() throws IOException {
        // 获取旧的验证码，注意不使用 getUnchecked, 避免重新生成一个验证码
        String deviceId = ReqInfoContext.getReqInfo().getDeviceId();
        String oldCode = deviceCodeCache.getIfPresent(deviceId);
        SseEmitter lastSse = oldCode == null ? null : verifyCodeCache.getIfPresent(oldCode);
        if (lastSse != null) {
            lastSse.send("resend!");
            lastSse.send("init#" + oldCode);
            return oldCode;
        }
        return "fail";
    }

    /**
     * 刷新验证码
     *
     * @return
     * @throws IOException
     */
    public String refreshCode() throws IOException {
        String deviceId = ReqInfoContext.getReqInfo().getDeviceId();
        // 获取旧的验证码，注意不使用 getUnchecked, 避免重新生成一个验证码
        String oldCode = deviceCodeCache.getIfPresent(deviceId);
        SseEmitter lastSse = oldCode == null ? null : verifyCodeCache.getIfPresent(oldCode);
        if (lastSse == null) {
            log.info("last deviceId:{}, code:{}, sse closed!", deviceId, oldCode);
            deviceCodeCache.invalidate(deviceId);
            return null;
        }

        // 重新生成一个验证码
        deviceCodeCache.invalidate(deviceId);
        String newCode = deviceCodeCache.getUnchecked(deviceId);
        log.info("generate new loginCode! deviceId:{}, oldCode:{}, code:{}", deviceId, oldCode, newCode);

        lastSse.send("updateCode!");
        lastSse.send("refresh#" + newCode);
        verifyCodeCache.invalidate(oldCode);
        verifyCodeCache.put(newCode, lastSse);
        return newCode;
    }

    /**
     * 微信公众号登录
     *
     * @param verifyCode 用户输入的登录验证码
     * @return
     */
    public boolean login(String verifyCode) {
        // 通过验证码找到对应的长连接
        SseEmitter sseEmitter = verifyCodeCache.getIfPresent(verifyCode);
        if (sseEmitter == null) {
            return false;
        }

        String session = sessionHelper.genSession(ReqInfoContext.getReqInfo().getUser());
        try {
            // 登录成功，写入session
            sseEmitter.send(session);
            // 设置cookie的路径
            sseEmitter.send("login#" + LoginConstants.SESSION_KEY + "=" + session + ";path=/;");
            return true;
        } catch (Exception e) {
            log.error("登录异常: {}", verifyCode, e);
        } finally {
            sseEmitter.complete();
            verifyCodeCache.invalidate(verifyCode);
        }
        return false;
    }


    /**
     * 添加在线人数
     *
     * @param add 正数，表示添加在线人数；负数，表示减少在线人数
     * @return
     */
    public int incrOnlineUserCnt(int add) {
        return onlineUserCnt.addAndGet(add);
    }

    /**
     * 查询在线用户人数
     *
     * @return
     */
    public int getOnlineUserCnt() {
        return onlineUserCnt.get();
    }
}

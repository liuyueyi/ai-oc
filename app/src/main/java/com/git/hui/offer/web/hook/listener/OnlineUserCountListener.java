package com.git.hui.offer.web.hook.listener;


import com.git.hui.offer.components.env.SpringUtil;
import com.git.hui.offer.user.service.LoginService;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * 通过监听session来实现实时人数统计
 *
 * @author YiHui
 * @date 2023/3/26
 */
@WebListener
public class OnlineUserCountListener implements HttpSessionListener {
    /**
     * 新增session，在线人数统计数+1
     *
     * @param se
     */
    public void sessionCreated(HttpSessionEvent se) {
        HttpSessionListener.super.sessionCreated(se);
        SpringUtil.getBean(LoginService.class).incrOnlineUserCnt(1);
    }

    /**
     * session失效，在线人数统计数-1
     *
     * @param se
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSessionListener.super.sessionDestroyed(se);
        SpringUtil.getBean(LoginService.class).incrOnlineUserCnt(-1);
    }
}

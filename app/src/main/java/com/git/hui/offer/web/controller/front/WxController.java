package com.git.hui.offer.web.controller.front;

import cn.hutool.core.util.NumberUtil;
import com.git.hui.offer.constants.user.permission.Permission;
import com.git.hui.offer.constants.user.permission.UserRoleEnum;
import com.git.hui.offer.user.service.LoginService;
import com.git.hui.offer.user.service.RechargeService;
import com.git.hui.offer.user.service.UserService;
import com.git.hui.offer.web.model.wx.BaseWxMsgResVo;
import com.git.hui.offer.web.model.wx.WxTxtMsgReqVo;
import com.git.hui.offer.web.model.wx.WxTxtMsgResVo;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author YiHui
 * @date 2025/7/16
 */
@Slf4j
@Permission(role = UserRoleEnum.ALL)
@RestController
@RequestMapping(path = "/api/wx")
public class WxController {
    private final LoginService loginService;
    private final UserService userService;

    private final RechargeService rechargeService;

    @Autowired
    public WxController(LoginService loginService, UserService userService, RechargeService rechargeService) {
        this.loginService = loginService;
        this.userService = userService;
        this.rechargeService = rechargeService;
    }

    /**
     * 客户端与后端建立扫描二维码的长连接
     *
     * @return
     */
    @ResponseBody
    @GetMapping(path = "subscribe", produces = {org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribe(String deviceId) throws IOException {
        return loginService.subscribe();
    }


    /**
     * 刷新验证码
     *
     * @return
     * @throws IOException
     */
    @GetMapping(path = "/login/refresh")
    @ResponseBody
    public String refresh(String deviceId) throws IOException {
        return loginService.refreshCode();
    }


    /**
     * fixme: 做一个鉴权
     * 微信的响应返回
     * 本地测试访问: curl -X POST 'http://localhost:8080/api/wx/callback' -H 'content-type:application/xml' -d '<xml><URL><![CDATA[https://hhui.top]]></URL><ToUserName><![CDATA[一灰灰blog]]></ToUserName><FromUserName><![CDATA[demoUser1234]]></FromUserName><CreateTime>1655700579</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[login]]></Content><MsgId>11111111</MsgId></xml>' -i
     *
     * @param msg
     * @return
     */
    @PostMapping(path = "callback",
            consumes = {"application/xml", "text/xml"},
            produces = "application/xml;charset=utf-8")
    public BaseWxMsgResVo callBack(@RequestBody WxTxtMsgReqVo msg) {
        String content = msg.getContent();
        if ("subscribe".equals(msg.getEvent()) || "scan".equalsIgnoreCase(msg.getEvent())) {
            String key = msg.getEventKey();
            if (StringUtils.isNotBlank(key) && key.startsWith("qrscene_")) {
                // 带参数的二维码，扫描、关注事件拿到之后，直接登录，省却输入验证码这一步
                // fixme 带参数二维码需要 微信认证，个人公众号无权限
                String code = key.substring("qrscene_".length());
                userService.autoRegisterWxUserInfo(msg.getFromUserName());
                String ans = loginService.login(code) ? "登录成功" : "登录失败，请输入验证码";
                WxTxtMsgResVo res = new WxTxtMsgResVo();
                res.setContent(ans);
                fillResVo(res, msg);
                return res;
            }
        }

        if (NumberUtil.isNumber(content)) {
            // 验证码登录方式，首先自动注册一个用户；然后再实现登录跳转
            userService.autoRegisterWxUserInfo(msg.getFromUserName());
            WxTxtMsgResVo res = new WxTxtMsgResVo();
            res.setContent(loginService.login(content) ? "登录成功" : "验证码过期了，刷新验证码再试试吧~");
            fillResVo(res, msg);
            return res;
        } else {
            WxTxtMsgResVo res = new WxTxtMsgResVo();
            res.setContent("这个关键词没有触发任何逻辑哦~");
            fillResVo(res, msg);
            return res;
        }
    }

    private void fillResVo(BaseWxMsgResVo res, WxTxtMsgReqVo msg) {
        res.setFromUserName(msg.getToUserName());
        res.setToUserName(msg.getFromUserName());
        res.setCreateTime(System.currentTimeMillis() / 1000);
    }


    /**
     * 微信支付回调
     *
     * @param request
     * @return
     */
    @PostMapping(path = "payNotify")
    public ResponseEntity<?> wxPayCallback(HttpServletRequest request) {
        return rechargeService.payCallback(request, rechargeService::payed);
    }
}

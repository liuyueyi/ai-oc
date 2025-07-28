package com.git.hui.offer.web.hook.interceptor;


import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.context.UserBo;
import com.git.hui.offer.components.env.SpringUtil;
import com.git.hui.offer.constants.user.permission.Permission;
import com.git.hui.offer.constants.user.permission.UserRoleEnum;
import com.git.hui.offer.user.service.UserService;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.ResVo;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限管控拦截器
 *
 * @author yihui
 * @date 2025/7/15
 */
@Slf4j
@Component
public class PermissionCheckInterceptor implements HandlerInterceptor {
    @Value("${spring.ai.mcp.server.sse-endpoint:/sse}")
    private String sseUrl;
    @Value("${spring.ai.mcp.server.sse-message-endpoint:/mcp/messages}")
    private String msgUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Permission permission = handlerMethod.getMethod().getAnnotation(Permission.class);
            if (permission == null) {
                permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
            }

            if (permission == null || permission.role() == UserRoleEnum.ALL) {
                return true;
            }


            if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_NOTLOGIN)));
                response.getWriter().flush();
                return false;
            }

            if (permission.role() == UserRoleEnum.ADMIN
                    && UserRoleEnum.ADMIN != ReqInfoContext.getReqInfo().getUser().role()) {
                // 设置为无权限
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }

            if (permission.role() == UserRoleEnum.VIP
                    && UserRoleEnum.VIP != ReqInfoContext.getReqInfo().getUser().role()) {
                // 这里是会员专项的内容，无权访问
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_VIP_INFO)));
                response.getWriter().flush();
                return false;
            }
        } else if (!checkMcpPermission(request)) {
            // mcp 相关校验 - 无权访问
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_VIP_INFO)));
            response.getWriter().flush();
            return false;
        }
        return true;
    }

    private boolean checkMcpPermission(HttpServletRequest request) {
        if (!mcpUrl(request)) {
            // 不是mcp的请求，不做拦截
            return true;
        }
        // 表示是mcp的请求，需要进行权限管控
        String auth = request.getHeader("Authorization");
        if (StringUtils.isBlank(auth) || !auth.startsWith("Bearer ")) {
            return false;
        }
        // 根据token，获取用户信息，如果拿不到，则表明没有传用户身份，直接返回false，无权访问
        String token = auth.substring(7);
        UserBo user = SpringUtil.getBean(UserService.class).getUserByWxId(token);
        if (user == null) {
            return false;
        }

        // 设置用户上下文信息
        ReqInfoContext.getReqInfo().setUserId(user.userId());
        ReqInfoContext.getReqInfo().setUser(user);
        return true;
    }

    private boolean mcpUrl(HttpServletRequest request) {
        String reqUrl = request.getRequestURI();
        return reqUrl.equals(sseUrl) || reqUrl.equals(msgUrl);
    }
}

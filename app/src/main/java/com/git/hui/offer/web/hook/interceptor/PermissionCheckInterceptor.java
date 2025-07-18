package com.git.hui.offer.web.hook.interceptor;


import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.ResVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 权限管控拦截器
 *
 * @author yihui
 * @date 2025/7/15
 */
@Slf4j
//@Component
public class PermissionCheckInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Permission permission = handlerMethod.getMethod().getAnnotation(Permission.class);
            if (permission == null) {
                permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
            }

            if (permission == null || permission.role() == UserRole.ALL) {
                return true;
            }

            if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
                // 访问需要登录的页面时，直接跳转到登录界面
                response.sendRedirect("/");
                return false;
            }

            if (permission.role() == UserRole.ADMIN
                    && UserRole.ADMIN != ReqInfoContext.getReqInfo().getUser().role()) {
                // 设置为无权限
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }

            if (permission.role() == UserRole.VIP
                    && UserRole.VIP != ReqInfoContext.getReqInfo().getUser().role()) {
                // 这里是会员专项的内容，无权访问
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_VIP_INFO)));
                response.getWriter().flush();
                return false;
            }
        }
        return true;
    }
}

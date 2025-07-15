package com.git.hui.offer.web.extend;

import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.ResVo;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一返回结果封装
 *
 * @author YiHui
 * @date 2025/7/15
 */
@RestControllerAdvice
public class RespDataWrapper implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType() != ResVo.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ResVo<Object> res = ResVo.success(body);
        // 返回类型不是 String：直接返回
        if (returnType.getParameterType() != String.class) {
            return res;
        }

        // 返回类型是 String：不能直接返回，需要进行额外处理
        // 1. 将 Content-Type 设为 application/json ；返回类型是String时，默认 Content-Type = text/plain
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 2. 将 Result 转为 Json字符串 再返回
        // （否则会报错 java.lang.ClassCastException: ResVo cannot be cast to java.lang.String）
        return JsonUtil.toStr(res);
    }
}

package com.git.hui.offer.web.extend;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.web.model.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author YiHui
 * @date 2025/7/15
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    public ResVo<String> handleBizException(BizException e) {
        return ResVo.fail(e.getCode(), e.getMsg());
    }

    /**
     * 非预期异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResVo<String> handleException(Exception e) {
        log.warn("非预期异常", e);
        return ResVo.fail(500, "服务器开小差，请稍后再试试吧~", ExceptionUtil.stacktraceToString(e));
    }
}

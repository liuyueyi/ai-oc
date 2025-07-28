package com.git.hui.offer.user.service.pay;

import com.git.hui.offer.constants.user.ThirdPayWayEnum;
import com.git.hui.offer.user.model.PayCallbackBo;
import com.git.hui.offer.user.model.PrePayInfoResBo;
import com.git.hui.offer.user.model.ThirdPayOrderReqBo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.function.Function;

/**
 * 对接三方支付的API定义
 *
 * @author YiHui
 * @date 2024/12/6
 */
public interface ThirdPayIntegrationApi {

    boolean support(ThirdPayWayEnum payWay);

    /**
     * 下单
     *
     * @param payReq
     * @return
     */
    PrePayInfoResBo createOrder(ThirdPayOrderReqBo payReq);


    /**
     * 查询订单
     *
     * @param outTradeNo
     * @return
     */
    PayCallbackBo queryOrder(String outTradeNo);


    /**
     * 支付回调
     *
     * @param request
     * @return
     */
    PayCallbackBo payCallback(HttpServletRequest request);

    /**
     * 关单
     *
     * @param outTradeNo
     */
    void closeOrder(String outTradeNo);

    /**
     * 退款回调
     *
     * @param request        携带回传的请求参数
     * @param refundCallback 退款结果回调执行业务逻辑
     * @return
     * @throws IOException
     */
    default <T> ResponseEntity<?> refundCallback(HttpServletRequest request, Function<T, Boolean> refundCallback) {
        return ResponseEntity.ok(true);
    }
}

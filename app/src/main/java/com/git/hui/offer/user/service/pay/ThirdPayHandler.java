package com.git.hui.offer.user.service.pay;

import com.git.hui.offer.components.env.SpringUtil;
import com.git.hui.offer.constants.user.ThirdPayWayEnum;
import com.git.hui.offer.oc.model.PayCallbackBo;
import com.git.hui.offer.oc.model.PrePayInfoResBo;
import com.git.hui.offer.oc.model.ThirdPayOrderReqBo;
import com.git.hui.offer.user.service.pay.wx.NativeWxPayIntegration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

/**
 * 与三方支付服务交互的门面类
 *
 * @author YiHui
 * @date 2024/12/6
 */
@Service
public class ThirdPayHandler {
    private List<ThirdPayIntegrationApi> payServiceList;

    @Autowired
    public ThirdPayHandler(List<ThirdPayIntegrationApi> payServiceList) {
        this.payServiceList = payServiceList;
    }

    private ThirdPayIntegrationApi getPayService(ThirdPayWayEnum payWay) {
        return payServiceList.stream().filter(s -> s.support(payWay)).findFirst().orElse(SpringUtil.getBean(NativeWxPayIntegration.class));
    }

    public PrePayInfoResBo createPayOrder(ThirdPayOrderReqBo payReq) {
        return getPayService(payReq.getPayWay()).createOrder(payReq);
    }

    public PayCallbackBo queryOrder(String outTradeNo, ThirdPayWayEnum payWay) {
        return getPayService(payWay).queryOrder(outTradeNo);
    }

    @Transactional
    public PayCallbackBo payCallback(HttpServletRequest request, ThirdPayWayEnum payWay) {
        return getPayService(payWay).payCallback(request);
    }

    @Transactional
    public <T> ResponseEntity<?> refundCallback(HttpServletRequest request, ThirdPayWayEnum payWay, Function<T, Boolean> refundCallback) {
        return getPayService(payWay).refundCallback(request, refundCallback);
    }
}

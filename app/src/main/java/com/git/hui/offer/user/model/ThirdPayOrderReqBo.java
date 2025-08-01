package com.git.hui.offer.user.model;

import com.git.hui.offer.constants.user.ThirdPayWayEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 向三方支付平台下单的请求业务参数
 *
 * @author YiHui
 * @date 2024/12/3
 */
@Data
@Accessors(chain = true)
public class ThirdPayOrderReqBo {
    /**
     * 订单号（业务）
     */
    private String outTradeNo;
    /**
     * 用户openId, 对于h5支付场景下，没有这个参数
     */
    private String openId;
    /**
     * 订单描述
     */
    private String description;
    /**
     * 订单总金额，单位为分
     */
    private int total;

    /**
     * 支付方式
     */
    private ThirdPayWayEnum payWay;

    public ThirdPayOrderReqBo() {
    }

    public ThirdPayOrderReqBo(String outTradeNo, String description, int total) {
        this.outTradeNo = outTradeNo;
        this.description = description;
        this.total = total;
    }

    public ThirdPayOrderReqBo(String outTradeNo, String openId, String description, int total) {
        this.outTradeNo = outTradeNo;
        this.openId = openId;
        this.description = description;
        this.total = total;
    }
}

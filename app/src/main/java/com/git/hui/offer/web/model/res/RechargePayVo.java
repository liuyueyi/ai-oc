package com.git.hui.offer.web.model.res;

/**
 * @author YiHui
 * @date 2025/7/21
 */
public record RechargePayVo(
        // 支付id
        Long payId
        // 交易号
        , String tradeNo
        // 充值金额
        , String amount
        // 充值登记
        , Integer vipLevel
        // 预支付id
        , String prePayId
        // 预支付过期时间
        , Long prePayExpireTime) {
}

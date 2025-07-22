package com.git.hui.offer.web.model.res;

/**
 * @author YiHui
 * @date 2025/7/21
 */
public record RechargeRecordVo(
        // 支付id
        Long payId
        // 交易号
        , String tradeNo
        // 充值金额
        , String amount
        // 充值级别
        , Integer level
        // 充斥状态
        , Integer status
        // 支付成功时间
        , Long payTime
        // 三方交易号
        , String transactionId) {
}

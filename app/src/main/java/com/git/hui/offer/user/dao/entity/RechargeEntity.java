package com.git.hui.offer.user.dao.entity;

import com.git.hui.offer.constants.user.ThirdPayWayEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

/**
 * 用户充值记录
 *
 * @author YiHui
 * @date 2025/7/21
 */
@Data
@Accessors(chain = true)
// 动态更新字段
@DynamicUpdate
@Entity(name = "user_recharge")
public class RechargeEntity {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 充值金额，单位为分
     */
    @Column(name = "amount")
    private Integer amount;

    /**
     * 充值的会员等级：月卡、季卡、年卡、终身会员
     */
    @Column(name = "vip_level")
    private Integer vipLevel;

    /**
     * 支付状态 0-未支付 1-支付中 2-支付成功 3-支付失败
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 微信支付交易号，唯一，会推送给微信
     */
    @Column(name = "trade_no")
    private String tradeNo;

    /**
     * 支付方式：
     *
     * @see ThirdPayWayEnum#getValue()
     */
    @Column(name = "pay_way")
    private String payWay;

    /**
     * 微信支付创建订单回传的关键信息
     */
    @Column(name = "pre_pay_id")
    private String prePayId;
    /**
     * prePayId的过期时间戳
     */
    @Column(name = "pre_pay_expire_time")
    private Date prePayExpireTime;

    /**
     * 微信支付回调时间
     */
    @Column(name = "pay_callback_time")
    private Date payCallbackTime;

    /**
     * 第三方支付交易号
     */
    @Column(name = "third_trans_code")
    private String thirdTransCode;

    /**
     * 微信支付创建订单回传的关键信息
     */
    @Column(name = "create_time")
    private Date createTime;


    @Column(name = "update_time")
    private Date updateTime;
}

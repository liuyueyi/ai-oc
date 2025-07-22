package com.git.hui.offer.user.service;

import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.components.env.SpringUtil;
import com.git.hui.offer.components.id.IdUtil;
import com.git.hui.offer.constants.user.RechargeLevelEnum;
import com.git.hui.offer.constants.user.RechargeStatusEnum;
import com.git.hui.offer.constants.user.ThirdPayWayEnum;
import com.git.hui.offer.oc.model.PayCallbackBo;
import com.git.hui.offer.oc.model.PrePayInfoResBo;
import com.git.hui.offer.oc.model.ThirdPayOrderReqBo;
import com.git.hui.offer.user.convert.RechargeConvert;
import com.git.hui.offer.user.dao.entity.RechargeEntity;
import com.git.hui.offer.user.dao.repository.RechargeRepository;
import com.git.hui.offer.user.service.pay.ThirdPayHandler;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.res.RechargePayVo;
import com.git.hui.offer.web.model.res.RechargeRecordVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 充值服务
 *
 * @author YiHui
 * @date 2025/7/21
 */
@Slf4j
@Service
public class RechargeService {

    private final RechargeRepository rechargeRepository;

    private final ThirdPayHandler thirdPayHandler;

    private final UserService userService;

    public RechargeService(RechargeRepository rechargeRepository, ThirdPayHandler thirdPayHandler, UserService userService) {
        this.rechargeRepository = rechargeRepository;
        this.thirdPayHandler = thirdPayHandler;
        this.userService = userService;
    }

    /**
     * 查询成功、失败，支付中的充值记录
     * <p>
     * 一个人的充值记录有限，这里先不实现具体的翻页
     *
     * @return
     */
    public PageListVo<RechargeRecordVo> listRechargeRecords() {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        List<RechargeEntity> list = rechargeRepository.findByUserIdAndStatusInOrderByIdDesc(userId,
                List.of(RechargeStatusEnum.SUCCEED.getValue(),
                        RechargeStatusEnum.FAIL.getValue(),
                        RechargeStatusEnum.PAYING.getValue()));

        List<RechargeRecordVo> voList = RechargeConvert.toRecordList(list);
        return PageListVo.of(voList, list.size(), 1, list.size());
    }

    /**
     * 准备充值
     */
    public RechargePayVo toPay(RechargeLevelEnum vipLevel) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        List<RechargeEntity> list = rechargeRepository.findByUserIdAndVipLevelAndStatusInOrderByIdDesc(userId, vipLevel.getValue(), List.of(RechargeStatusEnum.NOT_PAY.getValue(), RechargeStatusEnum.PAYING.getValue()));
        // 如果存在支付中的，则不允许再次发起
        if (list.stream().anyMatch(s -> s.getStatus().equals(RechargeStatusEnum.PAYING.getValue()))) {
            throw new BizException(StatusEnum.REPEAT_PAY);
        }

        // 找到最近的一个待支付记录
        int amount = SpringUtil.getSiteConfig().getVipPrice().get(vipLevel.getValue());
        RechargeEntity entity = list.stream()
                .filter(s -> s.getStatus().equals(RechargeStatusEnum.NOT_PAY.getValue()))
                .findFirst()
                .orElse(null);
        if (entity == null) {
            // 不存在，则创建一个待支付记录
            Long payId = IdUtil.genId();
            entity = new RechargeEntity()
                    .setId(payId)
                    .setStatus(RechargeStatusEnum.NOT_PAY.getValue())
                    .setUserId(userId)
                    .setAmount(amount)
                    .setVipLevel(vipLevel.getValue())
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
        }

        if (checkPrePayIdValid(entity)) {
            // 合法，则直接返回
            return RechargeConvert.toVo(entity);
        }

        // 非法, 重新向微信支付获取预支付ID
        ThirdPayWayEnum payWay = ThirdPayWayEnum.WX_NATIVE;
        entity.setPayWay(payWay.getValue());
        entity.setTradeNo(IdUtil.genPayCode(payWay, entity.getId()));

        // 需要像微信重新创建支付订单，并且将结果反写到支付记录中
        ThirdPayOrderReqBo req = new ThirdPayOrderReqBo();
        req.setTotal(amount);
        req.setOutTradeNo(entity.getTradeNo());
        req.setDescription(entity.getUserId() + "的会员充值");
        // 目前只支持native支付
        req.setPayWay(payWay);
        PrePayInfoResBo res = thirdPayHandler.createPayOrder(req);
        entity.setPrePayId(res.getPrePayId());
        entity.setPrePayExpireTime(new Date(res.getExpireTime()));
        entity.setUpdateTime(new Date());
        rechargeRepository.saveAndFlush(entity);

        // 返回预支付信息
        return RechargeConvert.toVo(entity);
    }

    private boolean checkPrePayIdValid(RechargeEntity recharge) {
        if (recharge.getPrePayId() == null || recharge.getPrePayExpireTime() == null) {
            return false;
        }

        if (recharge.getPrePayExpireTime().getTime() < System.currentTimeMillis() - 60_000L) {
            return false;
        }

        return true;
    }


    /**
     * 支付中
     */
    @Transactional
    public boolean paying(Long rechargeId) {
        RechargeEntity entity = rechargeRepository.selectByIdForUpdate(rechargeId);
        if (entity == null) {
            return false;
        }

        // 主动查询一下支付状态
        try {
            PayCallbackBo bo = thirdPayHandler.queryOrder(entity.getTradeNo(), ThirdPayWayEnum.ofPay(entity.getPayWay()));
            if (bo.getPayStatus() == RechargeStatusEnum.SUCCEED || bo.getPayStatus() == RechargeStatusEnum.FAIL) {
                // 实际结果是支付成功/支付失败时，刷新下record对应的内容
                // 更新原来的支付状态为最新的结果
                entity.setStatus(bo.getPayStatus().getValue());
                entity.setPayCallbackTime(new Date(bo.getSuccessTime()));
                entity.setUpdateTime(new Date());
                entity.setThirdTransCode(bo.getThirdTransactionId());
                rechargeRepository.saveAndFlush(entity);
            } else {
                // 直接更新为支付中
                entity.setStatus(RechargeStatusEnum.PAYING.getValue());
                entity.setUpdateTime(new Date());
                rechargeRepository.saveAndFlush(entity);
            }
        } catch (Exception e) {
            log.error("查询三方支付状态出现异常: {}", JsonUtil.toStr(entity), e);
        }

        // 依然返回true，将支付状态设置为true
        return true;
    }

    @Transactional
    public boolean payed(PayCallbackBo transaction) {
        log.info("微信支付回调执行业务逻辑 {}", transaction);
        if (transaction.getOutTradeNo().startsWith("TEST-")) {
            // TestController 中关于测试支付的回调逻辑时，我们只通过消息进行通知用户即可
            long payUser = transaction.getPayId();
            return true;
        }
        // 更新支付状态
        RechargeEntity entity = rechargeRepository.selectByIdForUpdate(transaction.getPayId());
        if (entity == null || !Objects.equals(entity.getTradeNo(), transaction.getOutTradeNo())) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, "支付记录:" + transaction.getPayId());
        }

        if (Objects.equals(entity.getStatus(), transaction.getPayStatus().getValue())
                || RechargeStatusEnum.SUCCEED.getValue().equals(entity.getStatus())) {
            // 幂等，or已支付成功，不进行后续更新
            return true;
        }

        // 更新支付结果
        entity.setStatus(transaction.getPayStatus().getValue());
        entity.setPayCallbackTime(new Date(transaction.getSuccessTime()));
        entity.setUpdateTime(new Date());
        entity.setThirdTransCode(transaction.getThirdTransactionId());
        rechargeRepository.saveAndFlush(entity);


        if (transaction.getPayStatus() == RechargeStatusEnum.SUCCEED) {
            // fixme 更新用户的vip状态
            userService.updateUserVipInfo(entity.getUserId(), entity.getVipLevel());
        }
        return true;
    }

    public ResponseEntity<?> payCallback(HttpServletRequest request, Function<PayCallbackBo, Boolean> payCallback) {
        try {
            PayCallbackBo bo = thirdPayHandler.payCallback(request, ThirdPayWayEnum.WX_NATIVE);
            boolean ans = payCallback.apply(bo);
            if (ans) {
                // 处理成功，返回 200 OK 状态码
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                // 处理异常，返回 500 服务器内部异常 状态码
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("微信支付回调v3java失败={}", e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
}

package com.git.hui.offer.user.convert;

import com.git.hui.offer.user.dao.entity.RechargeEntity;
import com.git.hui.offer.util.PriceUtil;
import com.git.hui.offer.web.model.res.RechargePayVo;
import com.git.hui.offer.web.model.res.RechargeRecordVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YiHui
 * @date 2025/7/22
 */
public class RechargeConvert {

    public static RechargePayVo toVo(RechargeEntity entity) {
        return new RechargePayVo(entity.getId(), entity.getTradeNo(), PriceUtil.toYuanPrice(entity.getAmount()),
                entity.getVipLevel(), entity.getPrePayId(), entity.getPrePayExpireTime().getTime());
    }

    public static RechargeRecordVo toRecord(RechargeEntity entity) {
        return new RechargeRecordVo(entity.getId()
                , entity.getTradeNo()
                , PriceUtil.toYuanPrice(entity.getAmount())
                , entity.getVipLevel()
                , entity.getStatus()
                , entity.getPayCallbackTime() == null ? entity.getUpdateTime().getTime() : entity.getPayCallbackTime().getTime()
                , entity.getThirdTransCode());
    }

    public static List<RechargeRecordVo> toRecordList(List<RechargeEntity> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        return list.stream().map(RechargeConvert::toRecord).collect(Collectors.toList());
    }
}

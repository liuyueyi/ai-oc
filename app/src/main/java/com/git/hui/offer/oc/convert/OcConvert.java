package com.git.hui.offer.oc.convert;

import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import com.git.hui.offer.oc.dao.entity.OcInfoEntity;
import com.git.hui.offer.util.DateUtil;
import com.git.hui.offer.web.model.res.OcVo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 转换类
 *
 * @author YiHui
 * @date 2025/7/14
 */
public class OcConvert {

    public static OcInfoEntity toOc(OcDraftEntity draft) {
        return new OcInfoEntity()
                .setDraftId(draft.getId())
                .setCompanyName(draft.getCompanyName())
                .setCompanyType(draft.getCompanyType())
                .setJobLocation(draft.getJobLocation())
                .setRecruitmentType(draft.getRecruitmentType())
                .setRecruitmentTarget(draft.getRecruitmentTarget())
                .setPosition(draft.getPosition())
                .setDeliveryProgress(draft.getDeliveryProgress())
                // 日期转时间
                .setLastUpdatedTime(DateUtil.toDateOrNow(draft.getLastUpdatedTime()))
                .setDeadline(draft.getDeadline())
                .setRelatedLink(draft.getRelatedLink())
                .setJobAnnouncement(draft.getJobAnnouncement())
                .setInternalReferralCode(draft.getInternalReferralCode())
                .setRemarks(draft.getRemarks())
                .setState(draft.getState())
                .setCreateTime(draft.getCreateTime())
                .setUpdateTime(new Date());
    }

    public static OcVo toVo(OcInfoEntity entity) {
        return new OcVo()
                .setId(entity.getId())
                .setDraftId(entity.getDraftId())
                .setCompanyName(entity.getCompanyName())
                .setCompanyType(entity.getCompanyType())
                .setJobLocation(entity.getJobLocation())
                .setRecruitmentType(entity.getRecruitmentType())
                .setRecruitmentTarget(entity.getRecruitmentTarget())
                .setPosition(entity.getPosition())
                .setDeliveryProgress(entity.getDeliveryProgress())
                .setLastUpdatedTime(DateUtil.time2date(entity.getLastUpdatedTime().getTime()))
                .setDeadline(entity.getDeadline())
                .setRelatedLink(entity.getRelatedLink())
                .setJobAnnouncement(entity.getJobAnnouncement())
                .setInternalReferralCode(entity.getInternalReferralCode())
                .setRemarks(entity.getRemarks())
                .setState(entity.getState())
                .setCreateTime(entity.getCreateTime().getTime())
                .setUpdateTime(entity.getUpdateTime().getTime());
    }

    public static List<OcVo> toVoList(List<OcInfoEntity> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(entity -> toVo(entity)).collect(Collectors.toList());
    }
}

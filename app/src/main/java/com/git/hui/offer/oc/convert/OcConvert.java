package com.git.hui.offer.oc.convert;

import com.git.hui.offer.oc.dao.entity.GatherDraftOcEntity;
import com.git.hui.offer.oc.dao.entity.OcEntity;
import com.git.hui.offer.util.DateUtil;

import java.util.Date;

/**
 * 转换类
 *
 * @author YiHui
 * @date 2025/7/14
 */
public class OcConvert {

    public static OcEntity toOc(GatherDraftOcEntity draft) {
        return new OcEntity()
                .setDraftId(draft.getId())
                .setCompanyName(draft.getCompanyName())
                .setCompanyType(draft.getCompanyType())
                .setJobLocation(draft.getJobLocation())
                .setRecruitmentType(draft.getRecruitmentType())
                .setRecruitmentTarget(draft.getRecruitmentTarget())
                .setPosition(draft.getPosition())
                .setDeliveryProgress(draft.getDeliveryProgress())
                // 日期转时间
                .setLastUpdatedTime(DateUtil.toDate(draft.getLastUpdatedTime()))
                .setDeadline(draft.getDeadline())
                .setRelatedLink(draft.getRelatedLink())
                .setJobAnnouncement(draft.getJobAnnouncement())
                .setInternalReferralCode(draft.getInternalReferralCode())
                .setRemarks(draft.getRemarks())
                .setState(draft.getState())
                .setCreateTime(draft.getCreateTime())
                .setUpdateTime(new Date());
    }
}

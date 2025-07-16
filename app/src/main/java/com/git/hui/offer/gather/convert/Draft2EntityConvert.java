package com.git.hui.offer.gather.convert;

import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 转换工具
 *
 * @author YiHui
 * @date 2025/7/14
 */
public class Draft2EntityConvert {

    public static OcDraftEntity convert(GatherOcDraftBo bo) {
        return new OcDraftEntity()
                .setCompanyName(bo.companyName())
                .setCompanyType(bo.companyType())
                .setJobLocation(bo.jobLocation())
                .setRecruitmentType(bo.recruitmentType())
                .setRecruitmentTarget(bo.requirementTarget())
                .setPosition(bo.position())
                .setDeliveryProgress(bo.deliveryProgress())
                .setLastUpdatedTime(bo.lastUpdatedTime())
                .setDeadline(bo.deadline())
                .setRelatedLink(bo.relatedLink())
                .setJobAnnouncement(bo.jobAnnouncement())
                .setInternalReferralCode(bo.internalReferralCode())
                .setRemarks(bo.remarks())
                ;
    }

    public static List<OcDraftEntity> convert(List<GatherOcDraftBo> bos) {
        if (CollectionUtils.isEmpty(bos)) {
            return Collections.emptyList();
        }
        return bos.stream().map(Draft2EntityConvert::convert).toList();
    }

}

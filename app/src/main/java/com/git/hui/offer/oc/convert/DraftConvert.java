package com.git.hui.offer.oc.convert;

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
public class DraftConvert {

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
        // 注意下面返回的是不可编辑的列表
        return bos.stream().map(DraftConvert::convert).toList();
    }

}

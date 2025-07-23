package com.git.hui.offer.oc.service;

import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.constants.oc.OcStateEnum;
import com.git.hui.offer.oc.convert.OcConvert;
import com.git.hui.offer.oc.dao.entity.OcInfoEntity;
import com.git.hui.offer.oc.dao.repository.OcDraftRepository;
import com.git.hui.offer.oc.dao.repository.OcRepository;
import com.git.hui.offer.util.DateUtil;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.OcSaveReq;
import com.git.hui.offer.web.model.req.OcSearchReq;
import com.git.hui.offer.web.model.res.OcVo;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Service
public class OcService {
    private final OcDraftRepository draftRepository;
    private final OcRepository ocRepository;

    @Autowired
    public OcService(OcDraftRepository repository, OcRepository ocRepository) {
        this.draftRepository = repository;
        this.ocRepository = ocRepository;
    }


    // -------------------------------------------- oc 相关服务 ------------------------------------------

    public PageListVo<OcVo> searchOcList(OcSearchReq req) {
        if (req.getNotState() == null) {
            // 不支持查询已删除状态的数据
            req.setNotState(OcStateEnum.DELETED.getValue());
        }
        req.autoInitPage();
        PageListVo<OcInfoEntity> list = ocRepository.findList(req);
        List<OcVo> voList = OcConvert.toVoList(list.getList());
        return PageListVo.of(voList, list.getTotal(), req.getPage(), req.getSize());
    }

    public OcVo detail(Long id) {
        OcInfoEntity entity = ocRepository.getReferenceById(id);
        if (entity.getState() == null || entity.getState().equals(OcStateEnum.DELETED.getValue())) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, id);
        }

        return OcConvert.toVo(entity);
    }

    public boolean updateOc(OcSaveReq req) {
        OcInfoEntity entity = ocRepository.getReferenceById(req.getId());
        if (entity.getState() == null || entity.getState().equals(OcStateEnum.DELETED.getValue())) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, req.getId());
        }

        // 做增量更新
        if (StringUtils.isNotBlank(req.getCompanyName())) {
            entity.setCompanyName(req.getCompanyName());
        }
        if (StringUtils.isNotBlank(req.getCompanyType())) {
            entity.setCompanyType(req.getCompanyType());
        }
        if (StringUtils.isNotBlank(req.getLocation())) {
            entity.setJobLocation(req.getLocation());
        }
        if (StringUtils.isNotBlank(req.getRecruitmentType())) {
            entity.setRecruitmentType(req.getRecruitmentType());
        }
        if (StringUtils.isNotBlank(req.getRecruitmentTarget())) {
            entity.setRecruitmentTarget(req.getRecruitmentTarget());
        }
        if (StringUtils.isNotBlank(req.getPosition())) {
            entity.setPosition(req.getPosition());
        }
        if (StringUtils.isNotBlank(req.getLastUpdatedTime())) {
            entity.setLastUpdatedTime(DateUtil.toDateOrNow(req.getLastUpdatedTime()));
        }
        if (StringUtils.isNotBlank(req.getDeadline())) {
            entity.setDeadline(req.getDeadline());
        }
        if (StringUtils.isNotBlank(req.getRelatedLink())) {
            entity.setRelatedLink(req.getRelatedLink());
        }
        if (StringUtils.isNotBlank(req.getJobAnnouncement())) {
            entity.setJobAnnouncement(req.getJobAnnouncement());
        }
        if (StringUtils.isNotBlank(req.getInternalReferralCode())) {
            entity.setInternalReferralCode(req.getInternalReferralCode());
        }
        if (StringUtils.isNotBlank(req.getRemarks())) {
            entity.setRemarks(req.getRemarks());
        }
        if (req.getState() != null) {
            entity.setState(req.getState());
        }
        entity.setUpdateTime(new Date());
        ocRepository.saveAndFlush(entity);
        return true;
    }


    public boolean updateState(Long id, OcStateEnum state) {
        OcInfoEntity entity = ocRepository.getReferenceById(id);
        if (entity.getState() == null || entity.getState().equals(OcStateEnum.DELETED.getValue())) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, id);
        }

        if (entity.getState().equals(state)) {
            return true;
        }
        entity.setState(state.getValue());
        entity.setUpdateTime(new Date());
        ocRepository.saveAndFlush(entity);
        return true;
    }
}

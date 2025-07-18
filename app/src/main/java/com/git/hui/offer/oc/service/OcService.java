package com.git.hui.offer.oc.service;

import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.constants.oc.DraftProcessEnum;
import com.git.hui.offer.constants.oc.DraftStateEnum;
import com.git.hui.offer.constants.oc.OcStateEnum;
import com.git.hui.offer.oc.convert.OcConvert;
import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import com.git.hui.offer.oc.dao.entity.OcInfoEntity;
import com.git.hui.offer.oc.dao.repository.OcDraftRepository;
import com.git.hui.offer.oc.dao.repository.OcRepository;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DraftOcUpdateReq;
import com.git.hui.offer.web.model.req.DraftSearchReq;
import com.git.hui.offer.web.model.req.OcSearchReq;
import com.git.hui.offer.web.model.res.OcVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (req.getState() == null) {
            req.setState(OcStateEnum.PUBLISHED.getValue());
        }
        if (req.getNotState() != null) {
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
}

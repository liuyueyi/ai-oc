package com.git.hui.offer.oc.service;

import com.git.hui.offer.constants.oc.DraftProcessEnum;
import com.git.hui.offer.constants.oc.DraftStateEnum;
import com.git.hui.offer.constants.oc.OcStateEnum;
import com.git.hui.offer.oc.convert.OcConvert;
import com.git.hui.offer.oc.dao.entity.GatherDraftOcEntity;
import com.git.hui.offer.oc.dao.entity.OcEntity;
import com.git.hui.offer.oc.dao.repository.GatherOcDraftRepository;
import com.git.hui.offer.oc.dao.repository.OcRepository;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DraftOcUpdateReq;
import com.git.hui.offer.web.model.req.OcSearchReq;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final GatherOcDraftRepository draftRepository;
    private final OcRepository ocRepository;

    @Autowired
    public OcService(GatherOcDraftRepository repository, OcRepository ocRepository) {
        this.draftRepository = repository;
        this.ocRepository = ocRepository;
    }

    public List<GatherDraftOcEntity> searchDraftList() {
        return draftRepository.findAll();
    }


    /**
     * 保存草稿数据列表
     * 此方法首先检查数据库中是否已存在相同的数据条目，以避免重复存储
     * 如果数据条目不存在，则将其状态设置为草稿，并标记为未处理，然后保存到数据库
     * 如果数据条目已存在，则更新其状态为未处理，并更新最后修改时间
     *
     * @param dataList 要保存的草稿数据列表，包含多个GatherOcDraftEntity对象
     */
    public void saveDraftDataList(List<GatherDraftOcEntity> dataList) {
        // 保存数据之前，需要先从数据库中做一个去重
        // 以公司名称 + 工作地点 + 更新时间 + 岗位 作为去重条件
        for (GatherDraftOcEntity data : dataList) {
            List<GatherDraftOcEntity> existingRecords = draftRepository.findByUniqueKey(
                    data.getCompanyName(),
                    data.getJobLocation(),
                    data.getLastUpdatedTime(),
                    data.getPosition()
            );

            if (existingRecords.isEmpty()) {
                // 没有重复数据，设置必要的状态和时间字段后，新增数据
                data.setToProcess(DraftProcessEnum.UNPROCESS.getValue());
                data.setState(DraftStateEnum.DRAFT.getValue());
                data.setCreateTime(new Date());
                data.setUpdateTime(new Date());
                draftRepository.save(data);
            } else {
                // 存在重复数据，更新已有的记录
                for (GatherDraftOcEntity existing : existingRecords) {
                    // 更新数据ID，保持状态，但将处理状态改为未处理
                    data.setId(existing.getId());
                    data.setState(existing.getState());
                    data.setToProcess(DraftProcessEnum.UNPROCESS.getValue());
                    data.setCreateTime(existing.getCreateTime());
                    data.setUpdateTime(new Date());
                    // 保存更新后的记录
                    draftRepository.save(existing);
                }
            }
        }
    }

    /**
     * 更新草稿状态
     * 此方法首先尝试根据提供的ID查找现有的草稿对象如果找不到，则创建一个新的草稿对象并设置创建时间
     * 无论找到与否，都将使用请求对象中的属性更新草稿对象的属性最后，保存更新后的草稿对象到数据库
     *
     * @param req 包含要更新的草稿信息的请求对象
     * @return 返回更新操作是否成功的结果
     */
    public boolean updateDraft(DraftOcUpdateReq req) {
        GatherDraftOcEntity draft = draftRepository.findById(req.getId()).orElse(null);
        if (draft == null) {
            // req -> entity
            draft = new GatherDraftOcEntity();
            BeanUtils.copyProperties(req, draft);
            draft.setCreateTime(new Date());
            draft.setToProcess(DraftProcessEnum.UNPROCESS.getValue());
        } else {
            // 执行更新
            BeanUtils.copyProperties(req, draft);
        }
        draft.setUpdateTime(new Date());
        draftRepository.saveAndFlush(draft);
        return true;
    }

    /**
     * 将草稿数据移动到正式数据中
     *
     * @param draftIds
     */
    public List<OcEntity> moveToOc(List<Long> draftIds) {
        // 1. 获取草稿数据
        List<GatherDraftOcEntity> draftData = draftRepository.findAllById(draftIds);

        // 2. 从正式库中，查询草稿数据，对于已经已经存在的，使用更细；不存在的，使用插入
        List<OcEntity> ocDatas = ocRepository.findByDraftIdInAndStateNot(draftIds, OcStateEnum.DELETED.getValue());
        Map<Long, OcEntity> ocMap = ocDatas.stream().collect(Collectors.toMap(OcEntity::getDraftId, v -> v));

        List<OcEntity> insertList = new ArrayList<>();
        List<OcEntity> updateList = new ArrayList<>();
        draftData.forEach(draft -> {
            OcEntity ocEntity = OcConvert.toOc(draft);
            ocEntity.setState(OcStateEnum.PUBLISHED.getValue());
            if (ocMap.containsKey(draft.getId())) {
                // 更新
                ocEntity.setId(ocMap.get(draft.getId()).getId());
                updateList.add(ocEntity);
            } else {
                // 插入
                insertList.add(ocEntity);
            }
        });

        // 3. 批量插入
        ocRepository.saveAll(insertList);

        // 4. 批量更新
        ocRepository.saveAll(updateList);

        // 5. 返回所有的变更数据
        insertList.addAll(updateList);
        return insertList;
    }


    // -------------------------------------------- oc 相关服务 ------------------------------------------

    public PageListVo<OcEntity> searchOcList(OcSearchReq req) {
        if (req.getState() == null) {
            req.setState(OcStateEnum.PUBLISHED.getValue());
        }
        req.autoInitPage();
        return ocRepository.findList(req);
    }
}

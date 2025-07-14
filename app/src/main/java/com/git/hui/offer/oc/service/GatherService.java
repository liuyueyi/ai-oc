package com.git.hui.offer.oc.service;

import com.git.hui.offer.model.oc.DraftProcessEnum;
import com.git.hui.offer.model.oc.DraftStateEnum;
import com.git.hui.offer.oc.dao.entity.GatherOcDraftEntity;
import com.git.hui.offer.oc.dao.repository.GatherOcDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Service
public class GatherService {
    private final GatherOcDraftRepository repository;

    @Autowired
    public GatherService(GatherOcDraftRepository repository) {
        this.repository = repository;
    }

    public List<GatherOcDraftEntity> getAll() {
        return repository.findAll();
    }

    /**
     * 保存草稿数据列表
     * 此方法首先检查数据库中是否已存在相同的数据条目，以避免重复存储
     * 如果数据条目不存在，则将其状态设置为草稿，并标记为未处理，然后保存到数据库
     * 如果数据条目已存在，则更新其状态为未处理，并更新最后修改时间
     *
     * @param dataList 要保存的草稿数据列表，包含多个GatherOcDraftEntity对象
     */
    public void saveDraftDataList(List<GatherOcDraftEntity> dataList) {
        // 保存数据之前，需要先从数据库中做一个去重
        // 以公司名称 + 工作地点 + 更新时间 + 岗位 作为去重条件
        for (GatherOcDraftEntity data : dataList) {
            List<GatherOcDraftEntity> existingRecords = repository.findByUniqueKey(
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
                repository.save(data);
            } else {
                // 存在重复数据，更新已有的记录
                for (GatherOcDraftEntity existing : existingRecords) {
                    // 更新数据ID，保持状态，但将处理状态改为未处理
                    data.setId(existing.getId());
                    data.setState(existing.getState());
                    data.setToProcess(DraftProcessEnum.UNPROCESS.getValue());
                    data.setCreateTime(existing.getCreateTime());
                    data.setUpdateTime(new Date());
                    // 保存更新后的记录
                    repository.save(existing);
                }
            }
        }
    }

}

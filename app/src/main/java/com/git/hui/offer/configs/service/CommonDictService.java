package com.git.hui.offer.configs.service;

import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.configs.dao.entity.CommonDictEntity;
import com.git.hui.offer.configs.dao.repository.CommonDictRepository;
import com.git.hui.offer.constants.common.BaseStateEnum;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DictSaveReq;
import com.git.hui.offer.web.model.req.DictSearchReq;
import com.git.hui.offer.web.model.res.CommonDictVo;
import com.git.hui.offer.web.model.res.DictItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YiHui
 * @date 2025/7/21
 */
@Slf4j
@Service
public class CommonDictService {

    private final CommonDictRepository commonDictRepository;

    public CommonDictService(CommonDictRepository commonDictRepository) {
        this.commonDictRepository = commonDictRepository;
    }

    /**
     * 适用于后台的字典管理列表
     *
     * @param req
     * @return
     */
    public PageListVo<CommonDictEntity> findListForAdmin(DictSearchReq req) {
        req.autoInitPage();
        return commonDictRepository.findList(req);
    }

    public boolean saveOrUpdateConfig(DictSaveReq req) {
        CommonDictEntity entity;
        if (req.id() != null) {
            entity = commonDictRepository.findById(req.id()).orElseThrow(() -> new BizException(StatusEnum.RECORDS_NOT_EXISTS));
        } else {
            entity = commonDictRepository.findFirstByAppAndKeyAndValue(req.app(), req.key(), req.value());
        }

        if (entity != null) {
            // 执行更新
            entity.setApp(req.app());
            entity.setKey(req.key());
            entity.setValue(req.value());
            entity.setIntro(req.intro());
            entity.setRemark(req.remark());
            entity.setScope(req.scope());
            entity.setUpdateTime(new Date());
        } else {
            entity = new CommonDictEntity();
            entity.setApp(req.app());
            entity.setScope(req.scope());
            entity.setKey(req.key());
            entity.setValue(req.value());
            entity.setIntro(req.intro());
            entity.setRemark(req.remark());
            entity.setState(req.state());
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
        }
        commonDictRepository.saveAndFlush(entity);
        return true;
    }

    public boolean deleteConfig(Long id) {
        commonDictRepository.deleteById(id);
        commonDictRepository.flush();
        return true;
    }

    public boolean updateState(Long id, Integer state) {
        CommonDictEntity entity = commonDictRepository.findById(id).orElse(null);
        if (entity == null) {
            throw new BizException(StatusEnum.RECORDS_NOT_EXISTS, "字典" + id);
        }

        if (entity.getState().equals(state)) {
            return true;
        }

        entity.setState(state);
        entity.setUpdateTime(new Date());
        commonDictRepository.saveAndFlush(entity);
        return true;
    }

    /**
     * 获取站点的字典列表
     *
     * @return
     */
    public List<CommonDictVo> queryPublicDictList() {
        List<CommonDictEntity> list = commonDictRepository.findAllByState(BaseStateEnum.NORMAL_STATE.getValue());
        Map<String, List<DictItemVo>> map = new HashMap<>();
        // 根据 app 进行分组，将 key, value, intro 构建 DictItemVo
        list.forEach(item -> {
            DictItemVo vo = new DictItemVo(item.getKey(), item.getValue(), item.getIntro());
            map.computeIfAbsent(item.getApp(), k -> new ArrayList<>()).add(vo);
        });
        List<CommonDictVo> ans = new ArrayList<>();
        map.forEach((app, items) -> {
            CommonDictVo vo = new CommonDictVo(app, items);
            ans.add(vo);
        });
        return ans;
    }
}

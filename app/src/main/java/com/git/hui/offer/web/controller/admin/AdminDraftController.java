package com.git.hui.offer.web.controller.admin;

import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import com.git.hui.offer.oc.dao.entity.OcInfoEntity;
import com.git.hui.offer.oc.service.OcService;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DraftOcUpdateReq;
import com.git.hui.offer.web.model.req.DraftSearchReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 草稿层控制器
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@Permission(role = UserRole.ADMIN)
@RestController
@RequestMapping(path = "/api/admin/draft")
public class AdminDraftController {

    private final OcService ocService;

    public AdminDraftController(OcService ocService) {
        this.ocService = ocService;
    }

    @GetMapping(path = "list")
    public PageListVo<OcDraftEntity> list(DraftSearchReq req) {
        return ocService.searchDraftList(req);
    }

    @GetMapping(path = "delete")
    public Boolean delete(Long draftId) {
        return ocService.deleteDraft(draftId);
    }

    /**
     * 更新数据
     *
     * @param req
     * @return
     */
    @PostMapping(path = "update")
    public Boolean update(@RequestBody DraftOcUpdateReq req) {
        Assert.notNull(req.getId(), "id can not be null");
        return ocService.updateDraft(req);
    }

    /**
     * 将草稿中的数据迁移到正式数据表中
     *
     * @param ids
     * @return
     */
    @PostMapping(path = "toOc")
    public List<OcInfoEntity> toOc(@RequestBody List<Long> ids) {
        Assert.notEmpty(ids, "请选择需要迁移的数据");
        return ocService.moveToOc(ids);
    }
}

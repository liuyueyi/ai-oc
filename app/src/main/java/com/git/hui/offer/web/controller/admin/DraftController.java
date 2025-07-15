package com.git.hui.offer.web.controller.admin;

import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.oc.dao.entity.GatherDraftOcEntity;
import com.git.hui.offer.oc.dao.entity.OcEntity;
import com.git.hui.offer.web.model.req.DraftOcUpdateReq;
import com.git.hui.offer.oc.service.OcService;
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
@RequestMapping(path = "/admin/draft")
public class DraftController {

    private final OcService ocService;

    public DraftController(OcService ocService) {
        this.ocService = ocService;
    }

    @GetMapping(path = "list")
    public List<GatherDraftOcEntity> list() {
        return ocService.searchDraftList();
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
    public List<OcEntity> toOc(@RequestBody List<Long> ids) {
        Assert.notEmpty(ids, "请选择需要迁移的数据");
        return ocService.moveToOc(ids);
    }
}

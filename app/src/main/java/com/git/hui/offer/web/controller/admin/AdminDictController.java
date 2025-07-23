package com.git.hui.offer.web.controller.admin;

import com.git.hui.offer.configs.dao.entity.CommonDictEntity;
import com.git.hui.offer.configs.service.CommonDictService;
import com.git.hui.offer.constants.user.permission.Permission;
import com.git.hui.offer.constants.user.permission.UserRoleEnum;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.DictSaveReq;
import com.git.hui.offer.web.model.req.DictSearchReq;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YiHui
 * @date 2025/7/21
 */
@Permission(role = UserRoleEnum.ADMIN)
@RestController
@RequestMapping(path = "/api/admin/dict")
public class AdminDictController {
    private final CommonDictService commonDictService;

    public AdminDictController(CommonDictService commonDictService) {
        this.commonDictService = commonDictService;
    }

    @RequestMapping(path = "list")
    public PageListVo<CommonDictEntity> list(DictSearchReq req) {
        return commonDictService.findListForAdmin(req);
    }

    @RequestMapping(path = "save")
    public Boolean save(@RequestBody DictSaveReq req) {
        return commonDictService.saveOrUpdateConfig(req);
    }

    /**
     * 删除配置
     *
     * @param id
     * @return
     */
    @RequestMapping(path = "delete")
    public Boolean delete(@RequestParam("id") Long id) {
        return commonDictService.deleteConfig(id);
    }

    /**
     * 启用or禁用
     *
     * @param id
     * @param state
     * @return
     */
    @RequestMapping(path = "updateState")
    public Boolean updateState(@RequestParam("id") Long id, @RequestParam("state") Integer state) {
        return commonDictService.updateState(id, state);
    }
}

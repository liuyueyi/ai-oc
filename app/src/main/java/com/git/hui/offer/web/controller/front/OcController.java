package com.git.hui.offer.web.controller.front;

import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.constants.oc.OcStateEnum;
import com.git.hui.offer.oc.service.OcService;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.OcSearchReq;
import com.git.hui.offer.web.model.res.OcVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@Permission(role = UserRole.ALL)
@RestController
@RequestMapping(path = "/api/oc")
@CrossOrigin
public class OcController {
    private final OcService ocService;

    public OcController(OcService ocService) {
        this.ocService = ocService;
    }

    @RequestMapping(path = "list")
    public PageListVo<OcVo> list(OcSearchReq req) {
        // 前台接口，只支持查询已发布的数据
        req.setState(OcStateEnum.PUBLISHED.getValue());
        return ocService.searchOcList(req);
    }

    @GetMapping(path = "detail")
    public OcVo detail(Long id) {
        return ocService.detail(id);
    }
}

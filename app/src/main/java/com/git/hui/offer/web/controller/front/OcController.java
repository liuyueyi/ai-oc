package com.git.hui.offer.web.controller.front;

import com.git.hui.offer.components.context.ReqInfoContext;
import com.git.hui.offer.constants.oc.OcStateEnum;
import com.git.hui.offer.constants.user.permission.Permission;
import com.git.hui.offer.constants.user.permission.UserRoleEnum;
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
@Permission(role = UserRoleEnum.ALL)
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

        if (ReqInfoContext.getReqInfo().getUserId() == null) {
            // 未登录时，永远最多只返回5个
            req.setPage(1);
            req.setSize(5);
        }
        return ocService.searchOcList(req);
    }


    @Permission(role = UserRoleEnum.NORMAL)
    @GetMapping(path = "detail")
    public OcVo detail(Long id) {
        return ocService.detail(id);
    }
}

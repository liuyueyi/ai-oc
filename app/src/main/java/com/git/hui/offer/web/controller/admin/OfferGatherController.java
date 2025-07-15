package com.git.hui.offer.web.controller.admin;

import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.gather.service.OfferGatherService;
import com.git.hui.offer.web.model.req.GatherReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 获取offer信息的入口
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Permission(role = UserRole.ADMIN)
@RestController
@RequestMapping(path = "/admin/gather")
public class OfferGatherController {
    private final OfferGatherService offerGatherService;

    @Autowired
    public OfferGatherController(OfferGatherService offerGatherService) {
        this.offerGatherService = offerGatherService;
    }

    @RequestMapping(path = "submit")
    public List<GatherOcDraftBo> submit(@RequestBody(required = false) GatherReq req) {
        List list = offerGatherService.gatherInfo(req);
        return list;
    }
}

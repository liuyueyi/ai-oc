package com.git.hui.offer.gather.controller;

import com.git.hui.offer.gather.model.GatherReq;
import com.git.hui.offer.gather.service.OfferGatherService;
import com.git.hui.offer.util.json.JsonUtil;
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
@RestController
@RequestMapping(path = "/admin/gather")
public class OfferGatherController {
    private final OfferGatherService offerGatherService;

    @Autowired
    public OfferGatherController(OfferGatherService offerGatherService) {
        this.offerGatherService = offerGatherService;
    }

    @RequestMapping(path = "submit")
    public String submit(@RequestBody(required = false) GatherReq req) {
        List list = offerGatherService.gatherInfo(req);
        return JsonUtil.toStr(list);
    }
}

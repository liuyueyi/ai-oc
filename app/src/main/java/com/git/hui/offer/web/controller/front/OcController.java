package com.git.hui.offer.web.controller.front;

import com.git.hui.offer.oc.dao.entity.OcEntity;
import com.git.hui.offer.oc.service.OcService;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.req.OcSearchReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/oc")
public class OcController {
    private final OcService ocService;

    public OcController(OcService ocService) {
        this.ocService = ocService;
    }

    @RequestMapping(path = "list")
    public PageListVo<OcEntity> list(OcSearchReq req) {
        return ocService.searchOcList(req);
    }
}

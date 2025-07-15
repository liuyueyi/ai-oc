package com.git.hui.offer.oc.controller;

import com.git.hui.offer.oc.dao.entity.OcEntity;
import com.git.hui.offer.oc.service.OcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping(path = "list")
    public List<OcEntity> list() {
        return ocService.getOcList();
    }
}

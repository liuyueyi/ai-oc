package com.git.hui.offer.web.controller.front;

import com.git.hui.offer.components.permission.Permission;
import com.git.hui.offer.components.permission.UserRole;
import com.git.hui.offer.constants.user.RechargeLevelEnum;
import com.git.hui.offer.user.service.RechargeService;
import com.git.hui.offer.util.json.IntBaseEnum;
import com.git.hui.offer.web.model.PageListVo;
import com.git.hui.offer.web.model.res.RechargePayVo;
import com.git.hui.offer.web.model.res.RechargeRecordVo;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 充值相关
 *
 * @author YiHui
 * @date 2025/7/22
 */
@Permission(role = UserRole.NORMAL)
@RestController
@RequestMapping("/api/recharge")
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    /**
     * 准备支付
     *
     * @param vipLevel
     * @return
     */
    @RequestMapping("/toPay")
    public RechargePayVo toPay(@RequestParam("vipLevel") Integer vipLevel) {
        RechargeLevelEnum level = IntBaseEnum.getEnumByCode(RechargeLevelEnum.class, vipLevel);
        Assert.notNull(level, "充值等级不能为空");
        return rechargeService.toPay(level);
    }

    /**
     * 支付中： 前端直接告诉后台表明已经支付了，此时会通知后台主动到微信后台查一下支付状态
     *
     * @param rechargeId
     * @return
     */
    @RequestMapping("/paying")
    public boolean hasPayed(@RequestParam("rechargeId") Long rechargeId) {
        return rechargeService.paying(rechargeId);
    }

    /**
     * 查询充值记录
     *
     * @return 充值记录
     */
    @RequestMapping(value = "/listRecords", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageListVo<RechargeRecordVo> listRecords() {
        return rechargeService.listRechargeRecords();
    }
}

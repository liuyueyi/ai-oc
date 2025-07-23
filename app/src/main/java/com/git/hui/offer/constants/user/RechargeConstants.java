package com.git.hui.offer.constants.user;

import java.util.Map;

/**
 * 充值常量定义
 *
 * @author YiHui
 * @date 2025/7/23
 */
public interface RechargeConstants {
    String RECHARGE_APP = "recharge";

    String VIP_PRICE_KEY = "vipPrice";

    String MONTH_VIP_INTRO = "月卡会员";

    String QUARTER_VIP_INTRO = "季卡会员";

    String YEAR_VIP_INTRO = "年卡会员";

    String LIFE_VIP_INTRO = "终身会员";

    Map<String, RechargeLevelEnum> RECHARGE_LEVEL_MAP = Map.of(
            MONTH_VIP_INTRO, RechargeLevelEnum.MONTH,
            QUARTER_VIP_INTRO, RechargeLevelEnum.QUARTER,
            YEAR_VIP_INTRO, RechargeLevelEnum.YEAR,
            LIFE_VIP_INTRO, RechargeLevelEnum.LIFE
    );
}

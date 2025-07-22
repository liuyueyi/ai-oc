package com.git.hui.offer.components.id;


import com.git.hui.offer.constants.user.ThirdPayWayEnum;
import com.git.hui.offer.util.CompressUtil;
import com.git.hui.offer.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;


/**
 * @author YiHui
 * @date 2023/8/30
 */
public class IdUtil {
    /**
     * 默认的id生成器
     */
    public static IdProducer DEFAULT_ID_PRODUCER = new IdProducer(new IdGenerator());

    private static AtomicLong INCR = new AtomicLong((int) (Math.random() * 500));
    private static long lastTime = 0;

    /**
     * 生成全局id
     *
     * @return
     */
    public static Long genId() {
        return DEFAULT_ID_PRODUCER.genId();
    }

    /**
     * 生成字符串格式全局id
     *
     * @return
     */
    public static String genStrId() {
        return CompressUtil.int2str(genId());
    }


    /**
     * 生成支付的唯一code
     * 简化的规则：payWay前缀 + 计数器 + id后缀
     *
     * @return
     */
    public static String genPayCode(ThirdPayWayEnum payWay, Long id) {
        long now = System.currentTimeMillis();
        if (DateUtil.skipDay(lastTime, now)) {
            lastTime = now;
            INCR.set((int) (Math.random() * 500));
        }
        return payWay.getDesc() + String.format("%03d", INCR.addAndGet(1)) + "-" + id;
    }


    /**
     * 根据payCode 解析获取 payId
     *
     * @param code
     * @return
     */
    public static Long getPayIdFromPayCode(String code) {
        String[] str = StringUtils.split(code, "-");
        return Long.valueOf(str[str.length - 1]);
    }

}

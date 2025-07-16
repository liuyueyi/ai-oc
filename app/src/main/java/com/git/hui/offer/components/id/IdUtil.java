package com.git.hui.offer.components.id;


import com.git.hui.offer.util.CompressUtil;


/**
 * @author YiHui
 * @date 2023/8/30
 */
public class IdUtil {
    /**
     * 默认的id生成器
     */
    public static IdProducer DEFAULT_ID_PRODUCER = new IdProducer(new IdGenerator());


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

}

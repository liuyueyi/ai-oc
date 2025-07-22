package com.git.hui.offer.constants.gather;

import com.git.hui.offer.util.json.StringBaseEnum;
import lombok.Getter;

/**
 * 数据采集任务状态
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum GatherModelEnum implements StringBaseEnum {
    ZHIPU("ZhiPu", "智谱清言"),
    SPARK_LITE("SparkLite", "讯飞星火"),
    CHAT_GPT("ChatGPT", "ChatGPT"),
    DEEPSEEK("DeepSeek", "DeepSeek"),
    ;
    private final String value;
    private final String desc;

    GatherModelEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

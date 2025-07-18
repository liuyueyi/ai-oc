package com.git.hui.offer.constants.gather;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * 数据采集目标的类型
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum GatherTaskStateEnum implements IntBaseEnum {
    INIT(0, "未处理"),
    PROCESSING(1, "处理中"),
    SUCCEED(2, "已处理"),
    FAILED(3, "处理失败"),
    ;
    private final Integer value;
    private final String desc;

    GatherTaskStateEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

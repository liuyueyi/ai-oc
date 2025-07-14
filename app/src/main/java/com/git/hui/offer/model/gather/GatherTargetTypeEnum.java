package com.git.hui.offer.model.gather;

import com.git.hui.offer.util.json.IntBaseEnum;
import lombok.Getter;

/**
 * 数据采集目标的类型
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Getter
public enum GatherTargetTypeEnum implements IntBaseEnum {
    HTML_TEXT(1, "html文本"),
    TEXT(2, "纯文本"),
    HTTP_URL(3, "http链接"),
    EXCEL_FILE(4, "excel文件"),
    CSV_FILE(5, "csv文件"),
    IMAGE(6, "图片");
    private final Integer value;
    private final String desc;

    GatherTargetTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}

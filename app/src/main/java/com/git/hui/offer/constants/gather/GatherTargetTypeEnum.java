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
public enum GatherTargetTypeEnum implements IntBaseEnum {
    HTML_TEXT(1, "html文本", false),
    TEXT(2, "纯文本", false),
    HTTP_URL(3, "http链接", false),
    EXCEL_FILE(4, "excel文件", true),
    CSV_FILE(5, "csv文件", true),
    IMAGE(6, "图片", true);
    private final Integer value;
    private final String desc;
    private final Boolean file;

    GatherTargetTypeEnum(Integer value, String desc, boolean file) {
        this.value = value;
        this.desc = desc;
        this.file = file;
    }
}

package com.git.hui.offer.oc.mcp.model;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author YiHui
 * @date 2025/7/28
 */
@Data
public class McpReqDto {
    @ToolParam(description = "招聘类型：春招、秋招、秋招提前批、补录、暑期实习、寒假实习、日常实习、社招等", required = false)
    private String recruitmentType;

    @ToolParam(description = "招聘对象：如2025年毕业生、2026年毕业生、2027年毕业生", required = false)
    private String recruitmentTarget;

    @ToolParam(description = "职位名称：如研发工程师、运营等", required = false)
    private String position;

    @ToolParam(description = "职位地点：如武汉、全国等", required = false)
    private String jobLocation;

    @ToolParam(description = "职位类型：如央国企、外企、私企、事业单位、学校、银行等", required = false)
    private String companyType;
}

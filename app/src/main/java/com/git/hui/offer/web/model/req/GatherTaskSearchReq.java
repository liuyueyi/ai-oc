package com.git.hui.offer.web.model.req;

import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;
import com.git.hui.offer.constants.gather.GatherTaskStateEnum;
import lombok.Data;

/**
 * 请求查询实体
 *
 * @author YiHui
 * @date 2025/7/18
 */
@Data
public class GatherTaskSearchReq extends PageReq {
    /**
     * 任务列表
     */
    private Long taskId;

    /**
     * 模型
     */
    private String model;

    /**
     * 抓取类型
     *
     *  1, "html文本"
     *  2, "纯文本"
     *  3, "http链接"
     *  4, "excel文件"
     *  5, "csv文件"
     *  6, "图片"
     *
     * @see GatherTargetTypeEnum#getValue()
     */
    private Integer type;

    /**
     * 任务状态
     *   0, "未处理"
     *   1, "处理中"
     *   2, "已处理"
     *   3, "处理失败"
     *
     * @see GatherTaskStateEnum#getValue()
     */
    private Integer state;
}

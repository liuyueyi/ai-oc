package com.git.hui.offer.web.model.res;

import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ai数据录入采用异步方案
 *
 * @author YiHui
 * @date 2025/7/18
 */
@Data
@Accessors(chain = true)
// 动态更新字段
public class TaskVo {
    private Long taskId;

    /**
     * 抓取类型
     *
     * @see GatherTargetTypeEnum#getValue()
     */
    private Integer type;

    /**
     * 大语言模型
     */
    private String model;

    /**
     * 任务处理状态： 0 未处理 1 处理中 2 处理完成 3 处理失败
     */
    private Integer state;

    /**
     * 传入的数据
     * - 如果是文件，则这里存储文件转存到oss后的地址
     * - 如果不是文件，则直接存储用户传入的文本内容
     */
    private String content;

    /**
     * 处理计数
     */
    private Integer cnt;

    /**
     * 处理的结果，json格式，对应的value是 draft_oc 表的主键
     * 形如:
     * {
     * "insert": [1, 2, 3],
     * "update": [4, 5, 6]
     * }
     */
    private String result;

    /**
     * 开始处理的时间
     */
    private Long processTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;
}

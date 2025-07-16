package com.git.hui.offer.web.model.req;

import lombok.Data;

/**
 * 草稿列表查询
 *
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class DraftSearchReq extends PageReq {
    /**
     * 主键
     */
    private Long id;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司类型
     */
    private String companyType;
    /**
     * 工作地点
     */
    private String jobLocation;
    /**
     * 招聘类型
     */
    private String recruitmentType;
    /**
     * 招聘对象
     */
    private String recruitmentTarget;
    /**
     * 岗位
     */
    private String position;
    /**
     * 岗位更新时间
     */
    private String lastUpdatedTimeAfter;

    private String lastUpdatedTimeBefore;
    /**
     * 状态:
     * -1 删除
     * 0 草稿
     * 1 已发布
     */
    private Integer state;

    private Integer notState;
    /**
     * 0 表示这条记录已处理
     * 1 表示这条数据待处理
     */
    private Integer toProcess;
}

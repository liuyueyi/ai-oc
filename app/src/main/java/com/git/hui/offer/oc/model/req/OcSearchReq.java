package com.git.hui.offer.oc.model.req;

import lombok.Data;

/**
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class OcSearchReq {
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
     * 投递进度
     */
    private String deliveryProgress;
    /**
     * 岗位更新时间
     */
    private String lastUpdatedTime;
    /**
     * 投递截止
     */
    private String deadline;
    /**
     * 相关链接
     */
    private String relatedLink;
    /**
     * 招聘公告
     */
    private String jobAnnouncement;
    /**
     * 内推码
     */
    private String internalReferralCode;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态:
     * -1 删除
     * 0 草稿
     * 1 已发布
     */
    private Integer state;
    /**
     * 0 表示这条记录已处理
     * 1 表示这条数据待处理
     */
    private Integer toProcess;
}

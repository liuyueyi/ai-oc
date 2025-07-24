package com.git.hui.offer.web.model.req;

import lombok.Data;

/**
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class OcSaveReq extends PageReq {
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
     * 发布时间
     */
    private String lastUpdatedTime;
    /**
     * 状态:
     * -1 删除
     * 0 草稿
     * 1 已发布
     */
    private Integer state;

    private String deadline;

    private String relatedLink;

    private String jobAnnouncement;

    private String internalReferralCode;

    private String remarks;
}

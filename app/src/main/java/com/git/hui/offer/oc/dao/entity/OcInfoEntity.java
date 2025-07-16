package com.git.hui.offer.oc.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

/**
 * 正式对外提供的招聘信息数据
 *
 * @author YiHui
 * @date 2025/7/14
 */
@Data
@Accessors(chain = true)
// 动态更新字段
@DynamicUpdate
@Entity(name = "oc_info")
public class OcInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 草稿数据
     */
    @Column(name = "draft_id")
    private Long draftId;

    /**
     * 公司名称
     */
    @Column(name = "company_name")
    private String companyName;
    /**
     * 公司类型
     */
    @Column(name = "company_type")
    private String companyType;
    /**
     * 工作地点
     */
    @Column(name = "job_location")
    private String jobLocation;
    /**
     * 招聘类型
     */
    @Column(name = "recruitment_type")
    private String recruitmentType;
    /**
     * 招聘对象
     */
    @Column(name = "recruitment_target")
    private String recruitmentTarget;
    /**
     * 岗位
     */
    @Column(name = "position")
    private String position;
    /**
     * 投递进度
     */
    @Column(name = "delivery_progress")
    private String deliveryProgress;
    /**
     * 岗位更新时间
     */
    @Column(name = "last_updated_time")
    private Date lastUpdatedTime;
    /**
     * 投递截止
     */
    @Column(name = "deadline")
    private String deadline;
    /**
     * 相关链接
     */
    @Column(name = "related_link")
    private String relatedLink;
    /**
     * 招聘公告
     */
    @Column(name = "job_announcement")
    private String jobAnnouncement;
    /**
     * 内推码
     */
    @Column(name = "internal_referral_code")
    private String internalReferralCode;
    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 状态:
     * -1 删除
     * 0 隐藏
     * 1 已发布
     */
    @Column(name = "state")
    private Integer state;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

}

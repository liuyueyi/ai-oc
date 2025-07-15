package com.git.hui.offer.oc.model.res;

import com.git.hui.offer.model.oc.CompanyTypeEnum;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/15
 */
@Data
public class OcVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 草稿数据
     */
    private Long draftId;

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
    private List<String> jobLocation;
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
     * 0 隐藏
     * 1 已发布
     */
    private Integer state;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}

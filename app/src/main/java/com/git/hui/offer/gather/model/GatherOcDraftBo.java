package com.git.hui.offer.gather.model;

/**
 * 采集到的oc草稿数据
 *
 * @author YiHui
 * @date 2025/7/14
 */
public record GatherOcDraftBo(
        String companyName,         // 公司名称
        String companyType,         // 公司类型
        String jobLocation,         // 工作地点
        String recruitmentType,     // 招聘类型
        String requirementTarget,      // 招聘对象
        String position,            // 岗位(大都不限专业)
        String deliveryProgress,   // 投递进度
        String lastUpdatedTime,     // 更新时间
        String deadline,            // 投递截止
        String relatedLink,         // 相关链接
        String jobAnnouncement,     // 招聘公告
        String internalReferralCode,// 内推码
        String remarks             // 备注
) {
}

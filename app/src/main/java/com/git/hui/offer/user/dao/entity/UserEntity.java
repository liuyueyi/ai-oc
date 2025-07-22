package com.git.hui.offer.user.dao.entity;

import com.git.hui.offer.constants.user.permission.UserRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

/**
 * @author YiHui
 * @date 2025/7/15
 */
@Data
@Accessors(chain = true)
// 动态更新字段
@DynamicUpdate
@Entity(name = "user_info")
public class UserEntity {
    @Id
    private Long id;

    /**
     * 显示名
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * 用户邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 登录用户名
     */
    @Column(name = "login_name")
    private String loginName;

    /**
     * 登录密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 头像
     */
    @Column(name = "avatar")
    private String avatar;

    /**
     * 个人简介
     */
    @Column(name = "intro")
    private String intro;

    /**
     * 微信登录时，这里存储微信的三方id
     */
    @Column(name = "wx_id")
    private String wxId;

    /**
     * 用户角色
     *
     * @see UserRoleEnum#getValue()
     */
    @Column(name = "role")
    private Integer role;

    /**
     * 会员过期时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 状态 1 有效 -1 删除
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

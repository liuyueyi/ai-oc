package com.git.hui.offer.user.dao.entity;

import com.git.hui.offer.components.permission.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YiHui
 * @date 2025/7/15
 */
@Data
@Accessors(chain = true)
@Entity(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 显示名
     */
    @Column(name = "display_name")
    private String displayName;

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
     * 微信登录时，这里存储微信的三方id
     */
    @Column(name = "wx_id")
    private String wxId;

    /**
     * 用户角色
     *
     * @see UserRole#getValue()
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

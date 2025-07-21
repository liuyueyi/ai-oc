package com.git.hui.offer.configs.dao.entity;

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
 * 全局字典表
 *
 * @author YiHui
 * @date 2025/7/21
 */
@Data
@Accessors(chain = true)
@DynamicUpdate
@Entity(name = "common_dict")
public class CommonDictEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    private String app;

    /**
     * 配置作用域: 0 私有 1 公有
     */
    @Column(name = "scope")
    private Integer scope;

    /**
     * 配置键
     */
    @Column(name = "dict_key")
    private String key;

    /**
     * 配置值
     */
    @Column(name = "dict_value")
    private String value;

    /**
     * 配置说明
     */
    @Column(name = "dict_intro")
    private String intro;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 状态 1 有效  0 未启用
     */
    @Column(name = "state")
    private Integer state;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}

-- oc.`user` definition
CREATE TABLE `user_info`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `wx_id`        varchar(128) NOT NULL DEFAULT '' COMMENT '第三方用户ID',
    `display_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '昵称',
    `email`        varchar(128) NOT NULL DEFAULT '' COMMENT '邮箱',
    `login_name`   varchar(64)  NOT NULL DEFAULT '' COMMENT '登录用户名',
    `password`     varchar(64)  NOT NULL DEFAULT '' COMMENT '登录用户密码',
    `avatar`       varchar(128) NOT NULL DEFAULT '' COMMENT '用户头像',
    `intro`        varchar(512) NOT NULL DEFAULT '' COMMENT '个人简介',
    `role`         tinyint      NOT NULL DEFAULT '1' COMMENT '用户角色: 1-普通用户，2-VIP，3-管理员',
    `state`        tinyint      NOT NULL DEFAULT '0' COMMENT '状态：-1 删除 0 禁用 1 正常',
    `expire_time`  timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `create_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY            `ix_wx_id` (`wx_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='用户信息表';


-- 用户充值记录表
CREATE TABLE `user_recharge`
(
    `id`                  bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`             bigint unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
    `amount`              int(10) NOT NULL DEFAULT '0' COMMENT '充值金额，单位分',
    `vip_level`           tinyint     NOT NULL DEFAULT '0' COMMENT '充值等级：0-月卡 1-季卡 2-年卡 3-终身',
    `status`              tinyint     NOT NULL DEFAULT '0' COMMENT '支付状态 0-未支付 1-支付中 2-支付成功 3-支付失败',
    `trade_no`            varchar(64) NOT NULL DEFAULT '' COMMENT '微信支付交易号，唯一，会推送给微信',
    `pay_way`             varchar(16) NOT NULL DEFAULT 'wx_native' COMMENT '支付方式：wx_h5 | wx_jsapi | wx_native',
    `third_trans_code`    varchar(50) NULL DEFAULT NULL COMMENT '三方交易流水',
    `pre_pay_id`          varchar(256) NULL DEFAULT NULL COMMENT '微信支付创建订单回传的关键信息',
    `pre_pay_expire_time` timestamp NULL DEFAULT NULL COMMENT 'prePayId失效时间',
    `pay_callback_time`   timestamp NULL DEFAULT NULL COMMENT '支付成功时间',
    `create_time`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY                   `ix_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='用户充值记录表';



-- ================================================= 下面是任务采集 & oc 相关表
-- 采集任务表
CREATE TABLE `gather_task`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `model`        varchar(64) NOT NULL DEFAULT '' COMMENT '使用的模型',
    `content`      text NULL DEFAULT NULL COMMENT '传入的数据',
    `result`       varchar(1024) NULL DEFAULT NULL COMMENT '传入的数据',
    `cnt`          int         NOT NULL DEFAULT '0' COMMENT '抓取次数',
    `type`         tinyint     NOT NULL DEFAULT '0' COMMENT '抓取类型：1 html文本 2 文本 3 http链接 4 excel文件 5 csv文件 6 图片',
    `state`        tinyint     NOT NULL DEFAULT '0' COMMENT '任务处理状态： 0 未处理 1 处理中 2 处理完成 3 处理失败',
    `process_time` timestamp NULL DEFAULT null COMMENT '处理时间',
    `create_time`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY            `ix_state` (`state`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='采集任务';

-- 采集的草稿表，通常需要进行数据清洗，然后再灌入正式数据表中
CREATE TABLE `draft_oc`
(
    `id`                     bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `company_name`           varchar(128)  NOT NULL DEFAULT '' COMMENT '公司名称',
    `company_type`           varchar(128)  NOT NULL DEFAULT '' COMMENT '公司类型',
    `job_location`           varchar(512)  NOT NULL DEFAULT '' COMMENT '工作地点',
    `recruitment_type`       varchar(512)  NOT NULL DEFAULT '' COMMENT '招聘类型',
    `recruitment_target`     varchar(128)  NOT NULL DEFAULT '' COMMENT '招聘对象',
    `position`               varchar(1024) NOT NULL DEFAULT '' COMMENT '岗位',
    `delivery_progress`      varchar(128)  NOT NULL DEFAULT '' COMMENT '投递进度',
    `last_updated_time`      varchar(64)   NOT NULL DEFAULT '' COMMENT '岗位更新时间',
    `deadline`               varchar(128)  NOT NULL DEFAULT '' COMMENT '投递截止',
    `related_link`           varchar(512)  NOT NULL DEFAULT '' COMMENT '相关链接',
    `job_announcement`       varchar(512)  NOT NULL DEFAULT '' COMMENT '招聘公告',
    `internal_referral_code` varchar(32)   NOT NULL DEFAULT '' COMMENT '内推码',
    `remarks`                varchar(512)  NOT NULL DEFAULT '' COMMENT '备注',
    `state`                  tinyint       NOT NULL DEFAULT '0' COMMENT '状态：-1 删除 0 草稿 1 已发布',
    `to_process`             tinyint       NOT NULL DEFAULT '1' COMMENT '是否需要处理：0 已处理 1 待处理',
    `create_time`            timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY                      `ix_company_name` (`company_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='采集的oc信息';

-- 正式数据表
CREATE TABLE `oc_info`
(
    `id`                     bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `draft_id`               bigint unsigned NOT NULL DEFAULT '0' COMMENT '草稿id',
    `company_name`           varchar(128)  NOT NULL DEFAULT '' COMMENT '公司名称',
    `company_type`           varchar(128)  NOT NULL DEFAULT '' COMMENT '公司类型',
    `job_location`           varchar(512)  NOT NULL DEFAULT '' COMMENT '工作地点',
    `recruitment_type`       varchar(512)  NOT NULL DEFAULT '' COMMENT '招聘类型',
    `recruitment_target`     varchar(128)  NOT NULL DEFAULT '' COMMENT '招聘对象',
    `position`               varchar(1024) NOT NULL DEFAULT '' COMMENT '岗位',
    `delivery_progress`      varchar(128)  NOT NULL DEFAULT '' COMMENT '投递进度',
    `last_updated_time`      varchar(64)   NOT NULL DEFAULT '' COMMENT '岗位更新时间',
    `deadline`               varchar(128)  NOT NULL DEFAULT '' COMMENT '投递截止',
    `related_link`           varchar(512)  NOT NULL DEFAULT '' COMMENT '相关链接',
    `job_announcement`       varchar(512)  NOT NULL DEFAULT '' COMMENT '招聘公告',
    `internal_referral_code` varchar(32)   NOT NULL DEFAULT '' COMMENT '内推码',
    `remarks`                varchar(512)  NOT NULL DEFAULT '' COMMENT '备注',
    `state`                  tinyint       NOT NULL DEFAULT '0' COMMENT '状态：-1 删除 0 隐藏 1 已发布',
    `create_time`            timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY                      `ix_draft_id` (`draft_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='正式的oc信息';


-- ================================ 通用信息表
CREATE TABLE `common_dict`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `app`         VARCHAR(32)  not null DEFAULT "" COMMENT '应用标识',
    `scope`       tinyint      not null DEFAULT 0 COMMENT '配置作用域: 0 私有 1 公有',
    `dict_key`    VARCHAR(128) not null DEFAULT "" COMMENT '配置键',
    `dict_value`  VARCHAR(256) not null DEFAULT "" COMMENT '配置值',
    `dict_intro`  VARCHAR(256) not null DEFAULT "" COMMENT '配置说明',
    `remark`      VARCHAR(256) not null DEFAULT "" COMMENT '备注',
    `state`       tinyint      not null DEFAULT 1 COMMENT '状态: -1 删除 0 未启用 1 有效',
    `create_time` DATETIME     not null DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `ix_app` (`app`),
    KEY           `ix_dict_key` (`dict_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局字典表';

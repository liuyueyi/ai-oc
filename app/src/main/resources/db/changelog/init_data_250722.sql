-- 系统的初始化数据

--  创建两个账号：普通 + 管理员
INSERT INTO user_info
(id, wx_id, display_name, email, login_name, password, avatar, intro, `role`, state, expire_time, create_time, update_time)
VALUES (1, 'demoUser-login', '普通用户', '', '', '',
        'https://cdn.tobebetterjavaer.com/paicoding/avatar/0067.png', '', 1, 1, NULL, now(), now());
INSERT INTO user_info
(id, wx_id, display_name, email, login_name, password, avatar, intro, `role`, state, expire_time, create_time, update_time)
VALUES (2, 'demoUser-admin', '管理员', '', '', '',
        'https://cdn.tobebetterjavaer.com/paicoding/avatar/0061.png', '', 3, 1, NULL, now(), now());


-- 初始化数据字典


-- 服务端字典
INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('server', 2, 'BaseStateEnum', '-1', '已删除', '通用状态枚举', 1),
       ('server', 2, 'BaseStateEnum', '0', '禁用', '通用状态枚举', 1),
       ('server', 2, 'BaseStateEnum', '1', '正常', '通用状态枚举', 1)
;

-- 全局站点字典，给前端使用
INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('site', 0, 'env', 'dev', '环境', '用于前端区分当前的环境为开发，测试，还是生产', 1),
       ('site', 0, 'loginQrImg', 'http://weixin.qq.com/r/WxwpMefE-rqBraNS90lJ', '扫码登录二维码', '', 1),
       ('site', 0, 'webSiteName', '校招派', '站点名', '', 1),
       ('site', 0, 'webSiteHost', 'https://laigeoffer.cn', '网站地址', '', 1)
;

INSERT INTO common_dict
(app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES
   ('recharge', 0, 'vipPrice', '19.99', '月卡会员', '会员费用', 1),
   ('recharge', 0, 'vipPrice', '59.99', '季卡会员', '会员费用', 1),
   ('recharge', 0, 'vipPrice', '139.99', '年卡会员', '会员费用', 1),
   ('recharge', 0, 'vipPrice', '299.99', '终身会员', '会员费用', 1);

-- 不同业务领域字典，给前端使用
INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('dicts', 0, 'DictScopeEnum', '0', '公开配置', '字典作用域', 1),
       ('dicts', 0, 'DictScopeEnum', '1', '管理配置', '字典作用域', 1),
       ('dicts', 0, 'DictScopeEnum', '2', '服务配置', '字典作用域', 1)
;

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('dicts', 0, 'DictAppEnum', 'site', '全局-站点配置', '字典配置应用', 1),
       ('dicts', 0, 'DictAppEnum', 'server', '后台-服务配置', '字典配置应用', 1),
       ('dicts', 0, 'DictAppEnum', 'dicts', '业务-字典配置', '字典配置应用', 1),
       ('dicts', 0, 'DictAppEnum', 'gather', '业务-数据录入', '字典配置应用', 1),
       ('dicts', 0, 'DictAppEnum', 'user', '业务-用户相关', '字典配置应用', 1),
       ('dicts', 0, 'DictAppEnum', 'oc', '业务-职位相关', '字典配置应用', 1),
       ('dicts', 0, 'DictAppEnum', 'recharge', '业务-充值配置', '字典配置应用', 1)
;

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('gather', 0, 'GatherTargetTypeEnum', '1', 'html文本', '数据采集目标的类型', 1),
       ('gather', 0, 'GatherTargetTypeEnum', '2', '纯文本', '数据采集目标的类型', 1),
       ('gather', 0, 'GatherTargetTypeEnum', '3', 'http链接', '数据采集目标的类型', 1),
       ('gather', 0, 'GatherTargetTypeEnum', '4', 'excel文件', '数据采集目标的类型', 1),
       ('gather', 0, 'GatherTargetTypeEnum', '5', 'csv文件', '数据采集目标的类型', 1),
       ('gather', 0, 'GatherTargetTypeEnum', '6', '图片', '数据采集目标的类型', 1)
;

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('gather', 0, 'GatherModelEnum', 'ZhiPu', '智谱清言', '大模型', 1),
       ('gather', 0, 'GatherModelEnum', 'SparkLite', '讯飞星火', '大模型', 1),
       ('gather', 0, 'GatherModelEnum', 'ChatGPT', 'ChatGPT', '大模型', 1),
       ('gather', 0, 'GatherModelEnum', 'DeepSeek', 'DeepSeek', '大模型', 1)
;

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('gather', 0, 'GatherTaskStateEnum', '0', '未处理', '数据采集任务状态', 1),
       ('gather', 0, 'GatherTaskStateEnum', '1', '处理中', '数据采集任务状态', 1),
       ('gather', 0, 'GatherTaskStateEnum', '2', '已处理', '数据采集任务状态', 1),
       ('gather', 0, 'GatherTaskStateEnum', '3', '处理失败', '数据采集任务状态', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('oc', 0, 'CompanyTypeEnum', '0', '央国企', '公司类型', 1),
       ('oc', 0, 'CompanyTypeEnum', '1', '外企', '公司类型', 1),
       ('oc', 0, 'CompanyTypeEnum', '2', '私企', '公司类型', 1),
       ('oc', 0, 'CompanyTypeEnum', '3', '事业单位', '公司类型', 1),
       ('oc', 0, 'CompanyTypeEnum', '4', '银行', '公司类型', 1),
       ('oc', 0, 'CompanyTypeEnum', '5', '学校', '公司类型', 1);
INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('oc', 0, 'DraftProcessEnum', '0', '未处理', '采集数据是否待同步oc', 1),
       ('oc', 0, 'DraftProcessEnum', '1', '已处理', '采集数据是否待同步oc', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('oc', 0, 'DraftStateEnum', '-1', '未处理', '采集数据状态', 1),
       ('oc', 0, 'DraftStateEnum', '0', '未处理', '采集数据状态', 1),
       ('oc', 0, 'DraftStateEnum', '1', '已处理', '采集数据状态', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('oc', 0, 'OcStateEnum', '-1', '已删除', 'oc数据状态', 1),
       ('oc', 0, 'OcStateEnum', '0', '待编辑', 'oc数据状态', 1),
       ('oc', 0, 'OcStateEnum', '1', '已发布', 'oc数据状态', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('oc', 0, 'RecruitmentTypeEnum', '0', '社招', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '1', '春招', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '2', '秋招', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '3', '补录', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '4', '春招提前批', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '5', '秋招提前批', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '101', '日常实习', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '102', '暑期实习', '招聘类型', 1),
       ('oc', 0, 'RecruitmentTypeEnum', '103', '寒期实习', '招聘类型', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('oc', 0, 'RecruitmentTargetEnum', '2025年毕业生', '2025年毕业生', '招聘对象', 1),
       ('oc', 0, 'RecruitmentTargetEnum', '2026年毕业生', '2026年毕业生', '招聘对象', 1),
       ('oc', 0, 'RecruitmentTargetEnum', '2027年毕业生', '2027年毕业生', '招聘对象', 1),
       ('oc', 0, 'RecruitmentTargetEnum', '2024与2025毕业生', '2024与2025毕业生', '招聘对象', 1),
       ('oc', 0, 'RecruitmentTargetEnum', '2025与2026毕业生', '2025与2026毕业生', '招聘对象', 1),
       ('oc', 0, 'RecruitmentTargetEnum', '其他', '其他', '招聘对象', 1)
;

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('user', 0, 'UserRoleEnum', '1', '普通用户', '用户角色', 1),
       ('user', 0, 'UserRoleEnum', '2', '会员', '用户角色', 1),
       ('user', 0, 'UserRoleEnum', '3', '管理员', '用户角色', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('user', 0, 'RechargeLevelEnum', '0', '月会员', '充值层级', 1),
       ('user', 0, 'RechargeLevelEnum', '1', '季会员', '充值层级', 1),
       ('user', 0, 'RechargeLevelEnum', '2', '年会员', '充值层级', 1),
       ('user', 0, 'RechargeLevelEnum', '3', '终身会员', '充值层级', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('user', 0, 'RechargeStatusEnum', '0', '待支付', '充值状态', 1),
       ('user', 0, 'RechargeStatusEnum', '1', '支付中', '充值状态', 1),
       ('user', 0, 'RechargeStatusEnum', '2', '支付成功', '充值状态', 1),
       ('user', 0, 'RechargeStatusEnum', '3', '支付失败', '充值状态', 1);

INSERT INTO common_dict
    (app, `scope`, dict_key, dict_value, dict_intro, remark, state)
VALUES ('user', 0, 'ThirdPayWayEnum', 'wx_h5', '微信h5支付', '支付方式', 1),
       ('user', 0, 'ThirdPayWayEnum', 'wx_jsapi', '微信jsapi支付', '支付方式', 1),
       ('user', 0, 'ThirdPayWayEnum', 'wx_native', '微信native支付', '支付方式', 1);
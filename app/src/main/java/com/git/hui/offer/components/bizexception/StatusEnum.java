package com.git.hui.offer.components.bizexception;

import lombok.Getter;

/**
 * 异常码规范：
 * xxx - xxx - xxx
 * 业务 - 状态 - code
 * <p>
 * 业务取值
 * - 100 全局
 * - 200 文章相关
 * - 300 评论相关
 * - 400 用户相关
 * <p>
 * 状态：基于http status的含义
 * - 4xx 调用方使用姿势问题
 * - 5xx 服务内部问题
 * <p>
 * code: 具体的业务code
 *
 * @author YiHui
 * @date 2022/7/27
 */
@Getter
public enum StatusEnum {
    SUCCESS(0, "OK"),

    // -------------------------------- 通用

    // 全局传参异常
    ILLEGAL_ARGUMENTS(100_400_001, "参数异常"),
    ILLEGAL_ARGUMENTS_MIXED(100_400_002, "参数异常:%s"),

    // 全局权限相关
    FORBID_ERROR(100_403_001, "无权限"),

    FORBID_ERROR_MIXED(100_403_002, "无权限:%s"),
    FORBID_NOTLOGIN(100_403_003, "未登录"),
    FORBID_VIP_INFO(100_403_004, "这是会员专享内容哦~"),

    // 全局，数据不存在
    RECORDS_NOT_EXISTS(100_404_001, "记录不存在:%s"),

    // 系统异常
    UNEXPECT_ERROR(100_500_001, "非预期异常:%s"),

    // 图片相关异常类型
    UPLOAD_PIC_FAILED(100_500_002, "图片上传失败！"),

    // --------------------------------


    // --------------------------------

    // 用户相关异常
    LOGIN_FAILED_MIXED(400_403_001, "登录失败:%s"),
    USER_NOT_EXISTS(400_404_001, "用户不存在:%s"),
    USER_EXISTS(400_404_002, "用户已存在:%s"),

    // 重复支付
    REPEAT_PAY(400_500_001, "您已经有一笔充值记录在支付中了哦~"),


    // ---------------------------------
    // 模型相关
    MODEL_NOT_SUPPORT(500_400_002, "模型不支持"),
    MODEL_MISMATCH_SUPPORT(500_400_002, "没有匹配的模型可用"),
    ;

    private int code;

    private String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static boolean is5xx(int code) {
        return code % 1000_000 / 1000 >= 500;
    }

    public static boolean is403(int code) {
        return code % 1000_000 / 1000 == 403;
    }

    public static boolean is4xx(int code) {
        return code % 1000_000 / 1000 < 500;
    }
}

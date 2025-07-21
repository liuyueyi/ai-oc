package com.git.hui.offer.web.model.req;

/**
 * 配置更新保存请求
 *
 * @author YiHui
 * @date 2025/7/21
 */
public record DictSaveReq(Long id, String app, Integer scope, String key, String value, String intro, String remark, Integer state) {
}

package com.git.hui.offer.web.model.res;

import java.util.List;

/**
 * 前台使用的字典列表
 *
 * @author YiHui
 * @date 2025/7/21
 */
public record CommonDictVo(String app, List<DictItemVo> items) {
}

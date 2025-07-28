package com.git.hui.offer.web.model.res;

import java.util.Map;

/**
 * mcp服务配置
 *
 * @author YiHui
 * @date 2025/7/28
 */
public record McpConfigVo(String type, String url, String version, Map<String, String> headers) {
}

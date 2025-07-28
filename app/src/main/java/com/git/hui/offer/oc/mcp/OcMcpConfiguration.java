package com.git.hui.offer.oc.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mcp 注册
 *
 * @author YiHui
 * @date 2025/7/28
 */
@Configuration
public class OcMcpConfiguration {
    @Bean
    public ToolCallbackProvider dateProvider(OcMcpService dateService) {
        return MethodToolCallbackProvider.builder().toolObjects(dateService).build();
    }
}

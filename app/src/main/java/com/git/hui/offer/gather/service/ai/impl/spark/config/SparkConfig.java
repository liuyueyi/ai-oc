package com.git.hui.offer.gather.service.ai.impl.spark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author YiHui
 * @date 2025/7/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.ai.spark")
public class SparkConfig {
    private String baseUrl;

    private String apiKey;

    private SparkChat chat;
}

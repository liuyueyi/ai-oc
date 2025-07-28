package com.git.hui.offer.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 全局网站配置
 *
 * @author YiHui
 * @date 2025/7/17
 */
@Data
@ConfigurationProperties(prefix = "oc.site")
@Component
public class SiteConfig {
    /**
     * 登录的二维码图片地址
     */
    private String loginQrImg;
    /**
     * 网站名称
     */
    private String websiteName;

    /**
     * 网站logo
     */
    private String webSiteLogo;
    /**
     * 网站域名
     */
    private String webSiteHost;

    /**
     * 网站url地址（包含端口）
     */
    private String webSiteUrl;
}

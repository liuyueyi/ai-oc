package com.git.hui.offer.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oc.img")
public class ImgConfig {

    /**
     * 存储绝对路径：指的是硬盘的绝对路径前缀
     */
    private String absTmpPath;

    /**
     * 存储相对路径：指的是http访问的路径
     */
    private String webImgPath;
    /**
     * 访问图片的host
     */
    private String cdnHost;

    public String buildImgUrl(String url) {
        if (!url.startsWith(cdnHost)) {
            return cdnHost + url;
        }
        return url;
    }
}
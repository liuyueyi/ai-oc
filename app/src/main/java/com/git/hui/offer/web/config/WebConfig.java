package com.git.hui.offer.web.config;

import com.git.hui.offer.components.env.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * web配置
 *
 * @author YiHui
 * @date 2025/7/16
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private String[] configs;

    private String[] staticConfigs() {
        String template = "spring.web.resources.static-locations[%d]";
        int i = 0;
        List<String> list = new ArrayList<>();
        do {
            String item = SpringUtil.getConfig(String.format(template, i));
            if (item == null) {
                break;
            }
            list.add(item);
            i++;
        } while (true);
        return list.toArray(new String[0]);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (configs == null) {
            configs = staticConfigs();
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(configs)
                .setCachePeriod(0).resourceChain(true)
                .addResolver(new AbstractResourceResolver() {
                    @Override
                    protected Resource resolveResourceInternal(HttpServletRequest request,
                                                               String requestPath,
                                                               List<? extends Resource> locations, ResourceResolverChain chain) {
                        for (Resource location : locations) {
                            // 解决前端访问路径上没有携带.html，导致查询后台静态资源找不到的问题
                            try {
                                Resource resource = location.createRelative(requestPath);
                                if (resource.exists() && resource.isReadable()) {
                                    return resource;
                                }

                                // 尝试添加 .html 后缀
                                resource = location.createRelative(requestPath + ".html");
                                if (resource.exists() && resource.isReadable()) {
                                    return resource;
                                }
                            } catch (IOException ignored) {
                            }
                        }

                        // 最后 fallback 到 index.html（适用于 SPA）
                        return new ClassPathResource("static/index.html");
                    }

                    @Override
                    protected String resolveUrlPathInternal(String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
                        return requestPath;
                    }
                });
    }

}

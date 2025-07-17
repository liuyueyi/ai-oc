package com.git.hui.offer;

import com.git.hui.offer.web.config.SiteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@EntityScan
@ServletComponentScan
public class AiOcApplication implements ApplicationRunner {
    @Value("${server.port:8080}")
    private Integer webPort;
    @Autowired
    private SiteConfig siteConfig;

    public static void main(String[] args) {
        SpringApplication.run(AiOcApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (webPort != null) {
            String url = siteConfig.getWebSiteHost() + ":" + webPort;
            log.info("启动成功，点击进入首页: {}", url);
        }
    }
}

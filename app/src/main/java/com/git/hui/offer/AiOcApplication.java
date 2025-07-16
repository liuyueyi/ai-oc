package com.git.hui.offer;

import lombok.extern.slf4j.Slf4j;
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

    public static void main(String[] args) {
        SpringApplication.run(AiOcApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (webPort != null) {
            String url = "http://127.0.0.1:" + webPort;
            log.info("启动成功，点击进入首页: {}", url);
        }
    }
}

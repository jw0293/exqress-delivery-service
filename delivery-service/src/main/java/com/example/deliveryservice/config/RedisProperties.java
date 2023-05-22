package com.example.deliveryservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Getter @Setter
public class RedisProperties {

    private final Environment env;

    private String port;
    private String host;

    @PostConstruct
    private void initRedis(){
        this.host = env.getProperty("spring.redis.host");
        this.port = env.getProperty("spring.redis.port");

        log.info("Get Property : {}", env.getProperty("spring.redis.host"));
        log.info("Get Property : {}", env.getProperty("spring.redis.port"));

        log.info("this Property : {}", host);
        log.info("this Property : {}", port);
    }
}

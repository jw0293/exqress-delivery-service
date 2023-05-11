package com.example.deliveryservice.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Setter
@Getter
@ConfigurationProperties("com.example")
@RefreshScope
@ToString
public class MyConfig {

    private String profile;
    private String region;

}

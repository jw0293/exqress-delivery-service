package com.example.deliveryservice;

import com.example.deliveryservice.config.MyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(MyConfig.class)
public class DeliveryServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(DeliveryServiceApplication.class, args);
	}
}

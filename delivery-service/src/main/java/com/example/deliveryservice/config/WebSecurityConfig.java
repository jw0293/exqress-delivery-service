package com.example.deliveryservice.config;

import com.example.deliveryservice.security.AuthenticationFilter;
import com.example.deliveryservice.security.CustomAuthenticationProvider;
import com.example.deliveryservice.service.*;
import com.example.deliveryservice.utils.CookieUtils;
import com.example.deliveryservice.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final TokenUtils tokenService;
    private final CookieUtils cookieUtils;
    private final CorsFilter corsFilter;
    //private final HandlerExceptionResolver resolver;

    private final DeliveryServiceImpl deliveryService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf().disable().headers()
                .frameOptions().sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .addFilter(corsFilter)
                .csrf().ignoringRequestMatchers("/h2-console/**").disable()
                .authorizeHttpRequests().requestMatchers("/h2-console/**").permitAll();

        http.
                authorizeHttpRequests()
                .anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(deliveryService, bCryptPasswordEncoder);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(tokenService, deliveryService, redisTemplate);
        //authenticationFilter.setFilterProcessesUrl("/delivery/login");
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }


}

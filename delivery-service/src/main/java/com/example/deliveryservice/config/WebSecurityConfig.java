package com.example.deliveryservice.config;

import com.example.deliveryservice.security.AuthenticationFilter;
import com.example.deliveryservice.security.CustomAuthenticationProvider;
import com.example.deliveryservice.service.DeliveryService;
import com.example.deliveryservice.service.DeliveryServiceImpl;
import com.example.deliveryservice.service.TokenService;
import com.example.deliveryservice.service.TokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final TokenServiceImpl tokenService;
    private final DeliveryServiceImpl deliveryService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf().disable().headers()
                .frameOptions().sameOrigin()
                .and()
                .csrf().ignoringRequestMatchers("/h2-console/**").disable()
                .authorizeHttpRequests().requestMatchers("/h2-console/**").permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/**").permitAll()
                .and()
                .addFilter(getAuthenticationFilter());

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
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(tokenService, deliveryService);
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }


}

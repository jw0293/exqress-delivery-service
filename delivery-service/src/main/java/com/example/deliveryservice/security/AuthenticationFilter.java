package com.example.deliveryservice.security;

import com.example.deliveryservice.constants.AuthConstants;
import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.service.DeliveryService;
import com.example.deliveryservice.service.DeliveryServiceImpl;
import com.example.deliveryservice.service.TokenService;
import com.example.deliveryservice.service.TokenServiceImpl;
import com.example.deliveryservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenServiceImpl tokenService;
    private final DeliveryServiceImpl deliveryService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            log.info("Email : {}", creds.getEmail());
            log.info("Passwor : {}", creds.getPassword());

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        new ArrayList<>()
            ));

        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String deliveryName = ((User) authResult.getPrincipal()).getUsername();
        DeliveryDto deliveryDetails = deliveryService.getUserDetailsByEmail(deliveryName);

        String token = tokenService.createToken(deliveryDetails);

        log.info("Token : {}", token);

        response.addHeader(AuthConstants.AUTH_HEADER, token);
        response.addHeader(AuthConstants.USERID_HEADER, deliveryDetails.getDeliveryId());

    }
}

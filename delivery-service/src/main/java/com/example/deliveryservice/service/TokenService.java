package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.vo.request.RequestToken;
import com.example.deliveryservice.vo.response.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface TokenService {

    String createToken(DeliveryDto deliveryDto);
    ResponseEntity<ResponseData> reissue(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<ResponseData> logout(RequestToken tokenInfo);
}

package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.dto.DeliveryQRDto;
//import com.example.deliveryservice.dto.kafka.DeliveryInfoWithQRId;
import com.example.deliveryservice.dto.kafka.DeliveryInfoWithQRId;
import com.example.deliveryservice.vo.request.RequestLogin;
import com.example.deliveryservice.vo.request.RequestQRcode;
import com.example.deliveryservice.vo.response.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface DeliveryService extends UserDetailsService {

    DeliveryDto createUser(DeliveryDto userDto);
    DeliveryDto getUserDetailsByEmail(String email);
    boolean isDuplicatedUser(String email);
    String getDeliveryIdThroughRequest(HttpServletRequest request);

    DeliveryQRDto mappingQRcode(String deliveryId, RequestQRcode qRcode);
    ResponseEntity<ResponseData> logout(String accessToken);
    ResponseEntity<ResponseData> login(HttpServletRequest request, HttpServletResponse response, RequestLogin login);

    ResponseEntity<ResponseData> updateParcelCompleteState(RequestQRcode qRcode);

    DeliveryInfoWithQRId getDeliveryInfoThroughId(String deliveryId);
}

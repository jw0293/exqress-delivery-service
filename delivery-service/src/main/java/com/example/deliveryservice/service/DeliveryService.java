package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.entity.DeliveryEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface DeliveryService extends UserDetailsService {

    DeliveryDto createUser(DeliveryDto userDto);
    DeliveryDto getUserDetailsByEmail(String email);

    DeliveryDto getUserByUserId(String userId);
}

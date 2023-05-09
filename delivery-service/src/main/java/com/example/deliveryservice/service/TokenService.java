package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.entity.DeliveryEntity;

public interface TokenService {

    String createToken(DeliveryDto userDto);
}

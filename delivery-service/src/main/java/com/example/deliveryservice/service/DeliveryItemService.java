package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.entity.DeliveryItem;
import com.example.deliveryservice.vo.RequestItem;
import com.example.deliveryservice.vo.ResponseItem;
import org.springframework.http.ResponseEntity;

public interface DeliveryService {

    Iterable<DeliveryItem> getItemsByUserId(String userId);
    DeliveryDto createDeliveryItem(DeliveryDto deliveryDto);
}

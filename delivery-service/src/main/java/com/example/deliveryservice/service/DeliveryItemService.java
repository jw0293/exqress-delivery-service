package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryItemDto;
import com.example.deliveryservice.entity.DeliveryItem;

public interface DeliveryItemService {

    Iterable<DeliveryItem> getItemsByUserId(String userId);
    DeliveryItemDto createDeliveryItem(DeliveryItemDto deliveryDto);
}

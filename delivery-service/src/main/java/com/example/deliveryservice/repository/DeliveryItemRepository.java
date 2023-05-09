package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.DeliveryItem;
import org.springframework.data.repository.CrudRepository;

public interface DeliveryItemRepository extends CrudRepository<DeliveryItem, Long> {

    DeliveryItem findByDeliveryId(String deliveryId);
    Iterable<DeliveryItem> findByUserId(String userId);

}

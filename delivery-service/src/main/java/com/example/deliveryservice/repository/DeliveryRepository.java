package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.DeliveryEntity;
import org.springframework.data.repository.CrudRepository;

public interface DeliveryRepository extends CrudRepository<DeliveryEntity, Long> {

    DeliveryEntity findByEmail(String username);
    DeliveryEntity findByDeliveryId(String userId);
}

package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.entity.DeliveryItem;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.example.deliveryservice.vo.RequestItem;
import com.example.deliveryservice.vo.ResponseItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Override
    public Iterable<DeliveryItem> getItemsByUserId(String userId) {
        return deliveryRepository.findByUserId(userId);
    }

    @Override
    public DeliveryDto createDeliveryItem(DeliveryDto deliveryDto) {
        deliveryDto.setDeliveryId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryItem deliveryItem = mapper.map(deliveryDto, DeliveryItem.class);

        deliveryRepository.save(deliveryItem);

        return mapper.map(deliveryItem, DeliveryDto.class);
    }
}

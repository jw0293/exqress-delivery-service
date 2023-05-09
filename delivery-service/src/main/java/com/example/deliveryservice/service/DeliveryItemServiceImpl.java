package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryItemDto;
import com.example.deliveryservice.entity.DeliveryItem;
import com.example.deliveryservice.repository.DeliveryItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DeliveryItemServiceImpl implements DeliveryItemService {

    private final DeliveryItemRepository deliveryRepository;

    @Override
    public Iterable<DeliveryItem> getItemsByUserId(String userId) {
        return deliveryRepository.findByUserId(userId);
    }

    @Override
    public DeliveryItemDto createDeliveryItem(DeliveryItemDto deliveryDto) {
        deliveryDto.setDeliveryId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryItem deliveryItem = mapper.map(deliveryDto, DeliveryItem.class);

        deliveryRepository.save(deliveryItem);

        return mapper.map(deliveryItem, DeliveryItemDto.class);
    }
}

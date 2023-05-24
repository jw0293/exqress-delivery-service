package com.example.deliveryservice.dto.kafka;

import lombok.Data;

@Data
public class DeliveryInfoWithQRId {

    private String qrId;
    private String deliveryName;
    private String deliveryPhoneNumber;
    private String state = "배송 시작";
}

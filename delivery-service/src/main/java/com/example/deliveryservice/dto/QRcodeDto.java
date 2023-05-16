package com.example.deliveryservice.dto;

import com.example.deliveryservice.entity.Address;
import lombok.Data;

@Data
public class QRcodeDto {

    private String deliveryId;
    private String codeKey;
    private String receiverName;
    private String productName;
    private Address address;
}

package com.example.deliveryservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryItemDto implements Serializable {

    private String deliveryId;
    private String userId;

    private String invoiceNo;
    private String productName;
    private String receiverName;
    private String receiverPhoneNumber;
    private String address;

}

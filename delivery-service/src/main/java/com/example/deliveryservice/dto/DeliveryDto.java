package com.example.deliveryservice.dto;

import com.example.deliveryservice.entity.Address;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryDto implements Serializable {

    private String deliveryId;
    private String userId;

    private String invoiceNo;
    private String productName;
    private String receiverName;
    private String receiverPhoneNumber;
    private Address address;

}

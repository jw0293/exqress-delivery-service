package com.example.deliveryservice.vo.request;

import com.example.deliveryservice.entity.Address;
import lombok.Data;

@Data
public class RequestQRcode {

    private String qrId;
    private String receiverName;
    private String productName;
    private String invoiceNo;
    private Address address;
    private String receiverPhoneNumber;
    private boolean isComplete = false;
}

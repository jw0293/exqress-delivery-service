package com.example.deliveryservice.messagequeue.topic;

public interface KafkaTopic {
    String DELIVERY_START = "delivery_start";
    String DELIVERY_COMPLETE = "delivery_complete";
    String QRINFO_TO_DELIVERY_SERVICE = "QRInfo_to_deliveryservice";
}

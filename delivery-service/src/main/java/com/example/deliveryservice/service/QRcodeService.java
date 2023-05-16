package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.QRcodeDto;
import com.example.deliveryservice.vo.response.ResponseQr;

public interface QRcodeService {

    QRcodeDto createQRcode(QRcodeDto qRcodeDto);
    Iterable<ResponseQr> getQRcodeIdByDeliveryId(String deliveryId);
}

package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.QRcodeDto;
import com.example.deliveryservice.vo.response.ResponseQr;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

public interface QRcodeService {

    QRcodeDto createQRcode(QRcodeDto qRcodeDto);
    ResponseEntity<?> getQRcodeIdByDeliveryId(String deliveryId);
}

package com.example.deliveryservice.dto.kafka;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeliveryInfoWithQRId {

    @Schema(description = "QR코드 ID", nullable = false, example = "egjh14813fghasd")
    private String qrId;

    @Schema(description = "QR코드 ID", nullable = false, example = "egjh14813fghasd")
    private String deliveryName;
    private String deliveryPhoneNumber;
    private String state = "배송 시작";
}

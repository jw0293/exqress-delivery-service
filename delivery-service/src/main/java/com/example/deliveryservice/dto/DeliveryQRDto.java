package com.example.deliveryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeliveryQRDto {

    @Schema(description = "배송기사 ID", nullable = false, example = "gskjseiadw!#@5qe15")
    private String deliveryId;
    @Schema(description = "QR코드 ID", nullable = false, example = "egjh14813fghasd")
    private String qrId;
}

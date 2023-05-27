package com.example.deliveryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeliveryQRDto {

    @Schema(description = "배송기사 ID", nullable = false, example = "gskjseiadw!#@5qe15")
    private String deliveryId;
    @Schema(description = "운송장 번호", nullable = false, example = "454214123341")
    private String invoiceNo;
}

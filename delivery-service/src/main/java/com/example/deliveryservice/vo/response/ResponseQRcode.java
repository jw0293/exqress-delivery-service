package com.example.deliveryservice.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseQRcode {

    @Schema(description = "배송 기사 ID", nullable = false, example = "e1asijisdie913")
    private String deliveryId;
    @Schema(description = "QR코드 Key값", nullable = false, example = "sari9egj31235")
    private String codeKey;
}

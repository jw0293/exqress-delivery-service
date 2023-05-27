package com.example.deliveryservice.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseSuccessSignUp {
    @Schema(description = "배송기사 ID값", nullable = false, example = "dsfjeksna-21jf")
    private String deliveryId;
}

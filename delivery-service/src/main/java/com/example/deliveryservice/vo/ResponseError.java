package com.example.deliveryservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseError {

    @Schema(description = "오류 메세지", nullable = false)
    private String errorMessage;

    @Schema(description = "오류 코드", nullable = false)
    private String errorCode;

}

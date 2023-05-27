package com.example.deliveryservice.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseSuccessTokenReissue {
    @Schema(description = "Access Token", nullable = false, example = "")
    private String accessToken;
}

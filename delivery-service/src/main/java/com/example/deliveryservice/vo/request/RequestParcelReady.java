package com.example.deliveryservice.vo.request;

import com.example.deliveryservice.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestParcelReady {

    @Schema(description = "QR코드 ID", nullable = false, example = "egjh14813fghasd")
    private String qrId;
}

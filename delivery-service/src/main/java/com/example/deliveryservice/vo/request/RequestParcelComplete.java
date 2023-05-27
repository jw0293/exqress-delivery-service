package com.example.deliveryservice.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestParcelComplete {
    @Schema(description = "운송장 번호", nullable = false, example = "012584042")
    private String invoiceNo;
}

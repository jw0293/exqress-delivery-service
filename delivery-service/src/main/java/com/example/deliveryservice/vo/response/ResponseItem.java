package com.example.deliveryservice.vo.response;

import com.example.deliveryservice.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseItem {
    @Schema(description = "배송기사 ID", nullable = false, example = "1gfoeh9214")
    private String deliveryId;

    @Schema(description = "운송장 번호", nullable = false, example = "012584042")
    private String invoiceNo;

    @Schema(description = "배송 제품 이름", nullable = false, example = "강아지 간식")
    private String productName;

    @Schema(description = "수령인 이름", nullable = false, example = "신현식")
    private String receiverName;

    @Schema(description = "수령인 전화번호", nullable = false, example = "010-4123-2691")
    private String receiverPhoneNumber;

    @Schema(description = "배송 완료 여부", nullable = false, example = "True/False")
    private Boolean isComplete;

    @Schema(description = "수령인 주소", nullable = false, example = "서울 중구 장충로")
    private Address address;
}

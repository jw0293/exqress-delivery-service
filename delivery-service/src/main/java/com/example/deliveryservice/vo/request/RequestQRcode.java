package com.example.deliveryservice.vo.request;

import com.example.deliveryservice.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestQRcode {

    @Schema(description = "QR코드 ID", nullable = false, example = "egjh14813fghasd")
    private String qrId;
    @Schema(description = "수령인 이름", nullable = false, example = "신현식")
    private String receiverName;
    @Schema(description = "배송 제품 이름", nullable = false, example = "강아지 간식")
    private String productName;
    @Schema(description = "운송장 번호", nullable = false, example = "012584042")
    private String invoiceNo;
    @Schema(description = "수령인 주소", nullable = false, example = "{ \"city\" : \"서울\", \"street\" : \"중구로\", \"zipcode\" : \"48321\"}")
    private Address address;
    @Schema(description = "수령인 전화번호", nullable = false, example = "010-4123-2691")
    private String receiverPhoneNumber;
    @Schema(description = "배송 완료 여부", nullable = false, example = "false")
    private boolean isComplete;
}

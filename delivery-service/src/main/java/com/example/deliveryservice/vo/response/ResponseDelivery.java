package com.example.deliveryservice.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseDelivery {

    @Schema(description = "배송기사 이메일", nullable = false, example = "hongdon@dgu.ac.kr")
    private String email;

    @Schema(description = "배송기사 이름", nullable = false, example = "김재한")
    private String name;

    @Schema(description = "배송기사 ID값", nullable = false, example = "dsfjeksna-21jf")
    private String deliveryId;

    @Schema(description = "배송기사 전화번호", nullable = false, example = "010-4123-2691")
    private String phoneNumber;
}

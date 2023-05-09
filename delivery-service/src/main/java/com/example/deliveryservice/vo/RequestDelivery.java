package com.example.deliveryservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestDelivery {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    @Schema(description = "배송기사 이메일", nullable = false, example = "hongdon@dgu.ac.kr")
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name not be less than two characters")
    @Schema(description = "배송기사 이름", nullable = false, example = "김재한")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password not be less than two characters")
    @Schema(description = "회원가입 비밀번호", nullable = false, example = "test1234")
    private String pwd;

    @NotNull(message = "Phone-Number cannot be null")
    @Schema(description = "배송기사 전화번호", nullable = false, example = "010-4123-2691")
    private String phoneNumber;


}
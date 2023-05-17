package com.example.deliveryservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    @Schema(description = "도시", nullable = false, example = "서울")
    private String city;
    @Schema(description = "도로명 주소", nullable = false, example = "장안로312길")
    private String street;
    @Schema(description = "우편번호", nullable = false, example = "02532")
    private String zipcode;

    protected Address(){

    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}

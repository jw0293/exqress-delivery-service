package com.example.deliveryservice.vo.request;

import lombok.Data;

@Data
public class RequestToken {

    private String accessToken;
    private String refreshToken;

}

package com.example.deliveryservice.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseData {

    private String status;
    private String message;
    private Object data;
    private String accessToken;
}

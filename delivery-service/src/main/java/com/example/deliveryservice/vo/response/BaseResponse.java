package com.example.deliveryservice.vo.response;

import lombok.Data;

@Data
public class BaseResponse {

    private String status;
    private String message;
    private String accessToken;

    public BaseResponse(){
        this.status = "200";
        this.message  = "성공";
    }
    public BaseResponse(String status, String message){
        this.status = status;
        this.message = message;
    }
}

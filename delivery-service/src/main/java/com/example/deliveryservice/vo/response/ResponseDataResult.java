package com.example.deliveryservice.vo.response;

import lombok.Data;

@Data
public class ResponseDataResult<T> extends BaseResponse {
    private T data;

    public ResponseDataResult(T data){
        super();
        this.data = data;
    }
}

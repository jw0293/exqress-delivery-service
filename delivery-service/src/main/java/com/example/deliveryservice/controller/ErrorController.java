package com.example.deliveryservice.controller;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.vo.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/unauthorized")
    public String throwUnauthorizedError(){
        return "리다이렉트 시켰으니 여기서 Reissue로 다시 POST로 날려줘!";
    }
}

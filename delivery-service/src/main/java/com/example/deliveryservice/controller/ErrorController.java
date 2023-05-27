package com.example.deliveryservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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

package com.example.deliveryservice.controller;

import com.example.deliveryservice.config.MyConfig;
import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.service.DeliveryServiceImpl;
import com.example.deliveryservice.vo.RequestDelivery;
import com.example.deliveryservice.vo.ResponseDelivery;
import com.example.deliveryservice.vo.ResponseError;
import com.example.deliveryservice.vo.ResponseItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "배송 기사", description = "배송 기사 관련 API입니다.")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/")
public class DeliveryController {

    private final MyConfig myConfig;
    private final DeliveryServiceImpl deliveryService;

    @GetMapping("/config")
    public ResponseEntity<String> config() {
        System.out.println(myConfig);
        return ResponseEntity.ok(myConfig.toString());
    }

    @Operation(summary = "배송 기사 회원가입", description = "배송 기사가 회원가입을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDelivery.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/deliverys")
    public ResponseEntity createUser(@RequestBody RequestDelivery delivery){
        String email = delivery.getEmail();
        if(deliveryService.isDuplicatedUser(email)) {
            return ResponseEntity.status(409).body("이미 존재하는 회원입니다");
        }

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryDto deliveryDto = mapper.map(delivery, DeliveryDto.class);
        deliveryService.createUser(deliveryDto);

        ResponseDelivery responseDelivery = mapper.map(deliveryDto, ResponseDelivery.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDelivery);
    }

}

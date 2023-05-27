package com.example.deliveryservice.controller;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.config.MyConfig;
import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.service.DeliveryServiceImpl;
import com.example.deliveryservice.service.TokenServiceImpl;
import com.example.deliveryservice.vo.request.RequestDelivery;
import com.example.deliveryservice.vo.request.RequestLogin;
import com.example.deliveryservice.vo.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final TokenServiceImpl tokenService;
    private final DeliveryServiceImpl deliveryService;

    @Operation(summary = "배송 기사 회원가입", description = "배송 기사가 회원가입을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/signUp")
    public ResponseData<? extends Object> createUser(@RequestBody RequestDelivery delivery){
        String email = delivery.getEmail();
        if(deliveryService.isDuplicatedUser(email)) {
            return new ResponseData<>(StatusEnum.EXISTED.getStatusCode(), "이미 존재하는 회원입니다.", "", "");
        }

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryDto deliveryDto = mapper.map(delivery, DeliveryDto.class);
        deliveryService.createUser(deliveryDto);

        ResponseSuccessSignUp responseSuccessSignUp = mapper.map(deliveryDto, ResponseSuccessSignUp.class);

        return new ResponseData<>(StatusEnum.OK.getStatusCode(), "회원가입 성공", responseSuccessSignUp, "");
    }

    @Operation(summary = "Token 재발급", description = "토큰 재발급을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = " 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return tokenService.reissue(request, response);
    }


    @Operation(summary = "배송 기사 로그아웃", description = "배송 기사가 로그아웃을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/signOut")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String accessToken = tokenService.getAccessToken(request, response);
        return deliveryService.logout(accessToken);
    }

    @Operation(summary = "배송 기사 로그인", description = "배송 기사가 로그인을 시도합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/signIn")
    public ResponseEntity<?> login(@RequestBody RequestLogin login, HttpServletRequest request, HttpServletResponse response){
        ResponseEntity<ResponseData> responseData = deliveryService.login(request, response, login);

        return responseData;
    }

}

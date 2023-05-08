package com.example.deliveryservice.controller;

import com.example.deliveryservice.dto.DeliveryDto;
import com.example.deliveryservice.entity.DeliveryItem;
import com.example.deliveryservice.service.DeliveryService;
import com.example.deliveryservice.vo.RequestItem;
import com.example.deliveryservice.vo.ResponseError;
import com.example.deliveryservice.vo.ResponseItem;
import com.example.deliveryservice.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "배송기사", description = "배송 기사 서비스 관련 API입니다.")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/")
public class DeliveryController {

    private final Environment env;
    private final DeliveryService deliveryService;

    @GetMapping("/welcome")
    public ResponseEntity<String> hello(){
        log.error("Good!");
        return ResponseEntity.status(HttpStatus.OK).body("Welcome to delivery Service");
    }

    @GetMapping("/{userId}/items")
    public ResponseEntity<Result> getItems(@PathVariable("userId") String userId){
        Iterable<DeliveryItem> itemList = deliveryService.getItemsByUserId(userId);

        List<ResponseItem> responseItems = new ArrayList<>();
        itemList.forEach(v -> {
            responseItems.add(new ModelMapper().map(v, ResponseItem.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(new Result(responseItems.size(), responseItems));
    }

    @Operation(summary = "배송 물품 입력", description = "사용자가 주문한 배송 물품이 저장됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장성공", content = @Content(schema = @Schema(implementation = ResponseItem.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/{userId}/items")
    public ResponseEntity<ResponseItem> createDeliveryItem(@PathVariable("userId") String userId,
                                                           @RequestBody RequestItem itemDetails) {
        log.info("DELIVERY_SERVICE USER ID : {}", userId);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        DeliveryDto deliveryDto = mapper.map(itemDetails, DeliveryDto.class);
        deliveryDto.setUserId(userId);
        DeliveryDto deliveryItem = deliveryService.createDeliveryItem(deliveryDto);

        ResponseItem responseItem = mapper.map(deliveryItem, ResponseItem.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseItem);
    }
}

package com.example.deliveryservice.controller;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.dto.DeliveryQRDto;
//import com.example.deliveryservice.dto.kafka.DeliveryInfoWithQRId;
//import com.example.deliveryservice.messagequeue.KafkaProducer;
import com.example.deliveryservice.service.DeliveryServiceImpl;
import com.example.deliveryservice.service.QRcodeServiceImpl;
import com.example.deliveryservice.vo.Result;
import com.example.deliveryservice.vo.request.RequestQRcode;
import com.example.deliveryservice.vo.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QR코드", description = "QR코드 관련 API입니다.")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/")
public class QRcodeController {

    //private final KafkaProducer kafkaProducer;
    private final QRcodeServiceImpl qRcodeService;
    private final DeliveryServiceImpl deliveryService;
    private ModelMapper mapper;

    @PostConstruct
    public void initMapper(){
        mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Operation(summary = "배송 물품 조회", description = "택배 기사에게 할당된 배송 물품이 조회됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ResponseItem.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/parcels")
    public ResponseEntity<ResponseData> getParcelsByDelivery(HttpServletRequest request){
        // JWT에서 기사ID를 가져온다
        String deliveryId = deliveryService.getDeliveryIdThroughRequest(request);
        List<ResponseQr> qRcodeIdByDeliveryId = qRcodeService.getQRcodeIdByDeliveryId(deliveryId);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "QR코드 저장 성공", new Result(qRcodeIdByDeliveryId.size(), qRcodeIdByDeliveryId), ""), HttpStatus.OK);
    }

    @Operation(summary = "배송물품 택배 할당", description = "배송물품 택배가 할당되어 출발 상태로 전환됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "할당 및 출발 상태 전환 성2", content = @Content(schema = @Schema(implementation = ResponseItem.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/qr/ready")
    public ResponseEntity<ResponseData> setQRcodeAndReadyStatus(HttpServletRequest request, @RequestBody RequestQRcode qRcode){
        // JWT에서 기사ID를 가져온다
        String deliveryId = deliveryService.getDeliveryIdThroughRequest(request);
        log.info("DeliveryId : {}", deliveryId);
        // 기사ID와 QRcode정보와 ID를 Mapping해준다
        DeliveryQRDto deliveryQRDto = deliveryService.mappingQRcode(deliveryId, qRcode);

        //DeliveryInfoWithQRId deliveryInfoThroughId = deliveryService.getDeliveryInfoThroughId(deliveryId);
        //deliveryInfoThroughId.setQrId(qRcode.getQrId());
        /**
         * 배송기사가 QR코드를 스캔함
         * User Service에게 kafka를 통해 데이터(배송 기사 ID, QR_ID)를 전송하여 배송 시작으로 업데이트 요청
         **/
        //kafkaProducer.sendUserServiceQRid("qr_topic", deliveryInfoThroughId);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "QR코드 저장 성공", deliveryQRDto, ""), HttpStatus.OK);
    }

    @Operation(summary = "배송 완료", description = "배송 기사가 배송을 완료하였습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "완료 처리 성공", content = @Content(schema = @Schema(implementation = ResponseDelivery.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping("/qr/complete")
    public ResponseEntity<ResponseData> setQRcodeAndCompleteStatus(@RequestBody RequestQRcode qRcode){
        /**
         * 배송 기사가 버튼을 누름으로써 배송 완료 처리 변경
         * User Service에게 Kafka를 통해 데이터(QR_ID, 배송 완료)를 전송하여 배송 완료 단계로 업데이트 요청
         */
        return deliveryService.updateParcelCompleteState(qRcode);
    }
}

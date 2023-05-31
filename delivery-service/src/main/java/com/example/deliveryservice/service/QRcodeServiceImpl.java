package com.example.deliveryservice.service;

import com.example.deliveryservice.StatusEnum;
import com.example.deliveryservice.dto.DeliveryQRDto;
import com.example.deliveryservice.dto.QRcodeDto;
import com.example.deliveryservice.dto.kafka.DeliveryInfoWithQRId;
import com.example.deliveryservice.entity.DeliveryEntity;
import com.example.deliveryservice.entity.QRcode;
import com.example.deliveryservice.messagequeue.producer.KafkaProducer;
import com.example.deliveryservice.messagequeue.topic.KafkaTopic;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.example.deliveryservice.repository.QRcodeRepository;
import com.example.deliveryservice.vo.request.RequestParcelComplete;
import com.example.deliveryservice.vo.response.ResponseData;
import com.example.deliveryservice.vo.response.ResponseQr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class QRcodeServiceImpl implements QRcodeService{

    private final KafkaProducer kafkaProducer;
    private final DeliveryServiceImpl deliveryService;
    private final QRcodeRepository qRcodeRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public ResponseEntity<ResponseData> getQRcodeIdByDeliveryId(String deliveryId) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryId(deliveryId);
        List<QRcode> qRcodeList = deliveryEntity.getQRcodeList();
        List<ResponseQr> parcelInfos = new ArrayList<>();
        qRcodeList.forEach(v -> parcelInfos.add(new ModelMapper().map(v, ResponseQr.class)));
        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "배송 물품 조회 성공", parcelInfos, ""), HttpStatus.OK);
    }

    @Override
    public QRcodeDto createQRcode(QRcodeDto qRcodeDto) {
        String qrId = UUID.randomUUID().toString();
        qRcodeDto.setCodeKey(qrId);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        QRcode qRcode = mapper.map(qRcodeDto, QRcode.class);
        qRcodeRepository.save(qRcode);

        return mapper.map(qRcode, QRcodeDto.class);
    }

    public ResponseEntity<ResponseData> mappingQRcode(String deliveryId, String qrId) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryId(deliveryId);
        log.info("Delivery Entity Name : {}", deliveryEntity.getName());
        QRcode qRcode = qRcodeRepository.findByQrId(qrId);
        log.info("QRcode ID : {}", qRcode.getQrId());
        if(qRcode == null){
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "존재하지 않는 QR_ID입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }

        log.info("QR code Id : {}", qRcode.getQrId());

        // Relationship Mapping
        qRcode.setDeliveryEntity(deliveryEntity);
        deliveryEntity.getQRcodeList().add(qRcode);
        qRcode.setIsComplete("start");
        log.info("ReliationShip Mapping : QRcode <-> DeliveryEntity");

        DeliveryQRDto deliveryMapQr = new DeliveryQRDto();
        deliveryMapQr.setDeliveryId(deliveryEntity.getDeliveryId());
        deliveryMapQr.setInvoiceNo(qRcode.getInvoiceNo());

        deliveryRepository.save(deliveryEntity);
        qRcodeRepository.save(qRcode);
        // JPA 변경 감지를 통한 Database Save

        DeliveryInfoWithQRId kafkaDeliveryReadyInfo = deliveryService.getDeliveryInfoThroughId(deliveryId, "start");
        kafkaDeliveryReadyInfo.setQrId(qrId);
        kafkaProducer.sendUserSeriveForDeliveryState(KafkaTopic.DELIVERY_START, kafkaDeliveryReadyInfo);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "QR코드 저장 성공", "", ""), HttpStatus.OK);
    }

    public ResponseEntity<ResponseData> updateParcelCompleteState(String deliveryId, RequestParcelComplete requestParcelComplete) {
        QRcode qr = qRcodeRepository.findByInvoiceNo(requestParcelComplete.getInvoiceNo());
        if (qr == null) {
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "존재하지 않는 운송장 번호입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        if (qr.getIsComplete().equals("true")) {
            return new ResponseEntity<>(new ResponseData(StatusEnum.BAD_REQUEST.getStatusCode(), "이미 배송 완료된 물품입니다.", "", ""), HttpStatus.BAD_REQUEST);
        }
        qr.setIsComplete("complete");
        qRcodeRepository.save(qr);

        DeliveryInfoWithQRId deliveryInfoThroughId = deliveryService.getDeliveryInfoThroughId(deliveryId, "complete");
        deliveryInfoThroughId.setQrId(requestParcelComplete.getInvoiceNo());
        kafkaProducer.sendUserSeriveForDeliveryState(KafkaTopic.DELIVERY_COMPLETE, deliveryInfoThroughId);

        return new ResponseEntity<>(new ResponseData(StatusEnum.OK.getStatusCode(), "배송 완료로 상태 업데이트 완료", "", ""), HttpStatus.OK);
    }
}

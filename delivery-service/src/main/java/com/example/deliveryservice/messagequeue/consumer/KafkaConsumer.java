package com.example.deliveryservice.messagequeue.consumer;

import com.example.deliveryservice.entity.QRcode;
import com.example.deliveryservice.messagequeue.topic.KafkaTopic;
import com.example.deliveryservice.repository.QRcodeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private ObjectMapper objectMapper;
    private final QRcodeRepository qRcodeRepository;

    @PostConstruct
    public void initMapper(){
        objectMapper = new ObjectMapper();
    }


    @KafkaListener(topics = KafkaTopic.QRINFO_TO_DELIVERY_SERVICE)
    public void assignUser(String kafkaMessage){
        Map<Object, Object> map = new HashMap<>();
        try{
            map = objectMapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException ex){
            ex.printStackTrace();
        }
        QRcode qRcodeFromAdmin = getQRcodeFromAdmin(map);
        qRcodeRepository.save(qRcodeFromAdmin);
    }

    private QRcode getQRcodeFromAdmin(Map<Object, Object> map){
        QRcode qRcode = new QRcode();
        qRcode.setQrId((String) map.get("qrId"));
        qRcode.setAddress((String) map.get("address"));
        qRcode.setInvoiceNo((String) map.get("invoiceNo"));
        qRcode.setIsComplete((String) map.get("curState"));
        qRcode.setProductName((String) map.get("productName"));
        qRcode.setAddress((String) map.get("address"));
        qRcode.setReceiverName((String) map.get("receiverName"));
        qRcode.setReceiverPhoneNumber((String) map.get("receiverPhoneNumber"));

        return qRcode;
    }

}

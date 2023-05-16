package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.QRcodeDto;
import com.example.deliveryservice.entity.DeliveryEntity;
import com.example.deliveryservice.entity.QRcode;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.example.deliveryservice.repository.QRcodeRepository;
import com.example.deliveryservice.vo.response.ResponseQr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class QRcodeServiceImpl implements QRcodeService{

    private final QRcodeRepository qRcodeRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public List<ResponseQr> getQRcodeIdByDeliveryId(String deliveryId) {
        DeliveryEntity deliveryEntity = deliveryRepository.findByDeliveryId(deliveryId);
        List<QRcode> qRcodeList = deliveryEntity.getQRcodeList();
        List<ResponseQr> qrIdList = new ArrayList<>();
        qRcodeList.forEach(v -> {
            qrIdList.add(new ModelMapper().map(v, ResponseQr.class));
        });
        return qrIdList;
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
}

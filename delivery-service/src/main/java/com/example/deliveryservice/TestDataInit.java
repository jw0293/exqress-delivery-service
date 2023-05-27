package com.example.deliveryservice;

import com.example.deliveryservice.entity.QRcode;
import com.example.deliveryservice.repository.QRcodeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final QRcodeRepository qRcodeRepository;

    @PostConstruct
    @Transactional
    public void initData(){
        qRcodeRepository.save(new QRcode("egjh14813fghasd", "김재한", "010-2211-3322", "딜도", "2931753", "false", "서울 중구 장충로 와르르맨션 205호"));
    }
}

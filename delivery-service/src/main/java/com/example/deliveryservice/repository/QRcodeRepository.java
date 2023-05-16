package com.example.deliveryservice.repository;

import com.example.deliveryservice.entity.QRcode;
import org.springframework.data.repository.CrudRepository;

public interface QRcodeRepository extends CrudRepository<QRcode, Long> {
    QRcode findByQrId(String qrId);
}

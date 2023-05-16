package com.example.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_entity_id")
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String deliveryId;

    @Column(nullable = false)
    private String encryptedPwd;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "deliveryEntity")
    private List<QRcode> qRcodeList;

}

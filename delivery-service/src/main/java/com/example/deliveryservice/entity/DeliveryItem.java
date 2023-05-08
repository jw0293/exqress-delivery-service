package com.example.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "delivery_item")
public class DeliveryItem implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String invoiceNo;

    @Column(nullable = false, length = 20)
    private String productName;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false, length = 50, unique = true)
    private String receiverPhoneNumber;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String deliveryId;

    @Column
    private Boolean isComplete;

    @Embedded
    private Address address;
}



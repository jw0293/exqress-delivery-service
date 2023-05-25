package com.example.deliveryservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table
public class QRcode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qrcode_id")
    private Long id;

    @Column(nullable = false)
    private String qrId;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhoneNumber;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String invoiceNo;

    @Column(nullable = false)
    @ColumnDefault("false")
    private String isComplete;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "delivery_entity_id")
    private DeliveryEntity deliveryEntity;
}

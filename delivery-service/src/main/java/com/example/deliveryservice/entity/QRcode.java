package com.example.deliveryservice.entity;

import com.example.deliveryservice.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;

@Data
@Entity
@Table
public class QRcode extends BaseTimeEntity implements Serializable {

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

    @Column
    private String address;

    @ManyToOne
    @JoinColumn(name = "delivery_entity_id")
    private DeliveryEntity deliveryEntity;
}

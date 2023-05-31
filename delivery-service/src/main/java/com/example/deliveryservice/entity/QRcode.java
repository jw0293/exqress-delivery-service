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

    @Column(nullable = false, unique = true)
    private String qrId;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhoneNumber;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String invoiceNo;

    @Column
    @ColumnDefault("false")
    private String isComplete;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String company;

    @ManyToOne
    @JoinColumn(name = "delivery_entity_id")
    private DeliveryEntity deliveryEntity;

    public QRcode(String qrId, String receiverName, String receiverPhoneNumber, String productName, String invoiceNo, String isComplete, String address){
        this.qrId = qrId;
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.productName = productName;
        this.invoiceNo = invoiceNo;
        this.isComplete = isComplete;
        this.address = address;
    }

    public QRcode() {

    }
}

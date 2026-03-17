package com.cts.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Redemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long redemptionId;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Order order;

    @Lob
    private String promotionIds;

    @Lob
    private String campaignIds;

    private String couponCode;

    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

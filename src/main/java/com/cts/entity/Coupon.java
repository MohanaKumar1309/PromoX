package com.cts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    private String couponCode;
    private String couponName;
    private String description;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private Integer usageLimit;
    private Integer usageCount;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal amount;
    private BigDecimal minCartValue;
    private BigDecimal maxDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdBy;

    @PrePersist
    void prePersist() {
        if (this.status == null) {
            this.status = ApprovalStatus.PENDING;
        }
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
    }
}

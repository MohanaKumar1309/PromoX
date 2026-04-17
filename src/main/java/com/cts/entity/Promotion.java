package com.cts.entity;

import com.cts.enums.ApprovalStatus;
import com.cts.enums.DiscountType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal discountValue;
    private Integer minQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private Long createdBy;

    @PrePersist
    void prePersist() {
        if (this.status == null) {
            this.status = ApprovalStatus.PENDING;
        }
    }
}

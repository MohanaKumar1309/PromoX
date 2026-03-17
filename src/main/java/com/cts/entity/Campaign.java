package com.cts.entity;


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
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignId;

    private String campaignName;
    private String description;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private Integer minAge;
    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdBy;

    @PrePersist
    void prePersist() {
        if (this.status == null) {
            this.status = ApprovalStatus.PENDING;
        }
    }
}
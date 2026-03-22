package com.cts.dto;

import com.cts.enums.ApprovalStatus;
import com.cts.enums.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CouponGetDto {
    private Long couponId;
    private String couponCode;
    private String couponName;
    private String description;
    private ApprovalStatus status;
    private Integer usageLimit;
    private Integer usageCount;
    private DiscountType discountType;
    private BigDecimal amount;
    private BigDecimal minCartValue;
    private BigDecimal maxDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdBy;
}

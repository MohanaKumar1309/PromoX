package com.cts.dto;

import com.cts.enums.ApprovalStatus;
import com.cts.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionGetDto {
    private Long promotionId;
    private String name;
    private String description;
    private ApprovalStatus status;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private Integer minQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private Long createdBy;
    private List<Long> productIds;
    private List<Long> categoryIds;
}
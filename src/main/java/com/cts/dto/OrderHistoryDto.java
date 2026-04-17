package com.cts.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderHistoryDto {
    private Long orderId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String appliedPromotions;
    private String appliedCampaigns;
    private String appliedCoupon;
    private LocalDateTime createdAt;
}

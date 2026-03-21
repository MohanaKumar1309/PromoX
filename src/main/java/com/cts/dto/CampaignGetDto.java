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
public class CampaignGetDto {
    private Long campaignId;
    private String campaignName;
    private String description;
    private ApprovalStatus status;
    private Integer minAge;
    private Integer maxAge;
    private DiscountType discountType;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdBy;
    private List<Long> productIds;
    private List<Long> categoryIds;
}

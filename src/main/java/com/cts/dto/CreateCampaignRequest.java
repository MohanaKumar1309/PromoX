package com.cts.dto;


import com.cts.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class CreateCampaignRequest {
    @NotBlank
    private String campaignName;
    private String description;

    @NotNull
    private Integer minAge;

    @NotNull
    private Integer maxAge;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private List<Long> productIds;
    private List<Long> categoryIds;
}

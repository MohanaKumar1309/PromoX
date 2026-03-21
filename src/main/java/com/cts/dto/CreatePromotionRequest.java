package com.cts.dto;

import com.cts.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class CreatePromotionRequest {
    @NotBlank
    private String name;
    private String description;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal discountValue;

    private BigDecimal minAmount;
    private Integer minQuantity;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotBlank
    private String type;

    private List<Long> productIds;
    private List<Long> categoryIds;
}
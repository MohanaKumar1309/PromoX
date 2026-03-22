package com.cts.dto;

import com.cts.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCouponRequest {
    @NotBlank
    private String couponCode;

    @NotBlank
    private String couponName;

    private String description;

    @NotNull
    private Integer usageLimit;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal minCartValue;

    private BigDecimal maxDiscount;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}

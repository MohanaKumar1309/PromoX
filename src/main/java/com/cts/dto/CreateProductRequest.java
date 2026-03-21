package com.cts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class CreateProductRequest {
    @NotNull
    private Long categoryId;

    @NotBlank
    private String name;

    @NotBlank
    private String sku;

    @NotNull
    private BigDecimal price;

    private String imageUrl;
}

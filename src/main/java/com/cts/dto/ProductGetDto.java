package com.cts.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductGetDto {
    private Long productId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String sku;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;
}

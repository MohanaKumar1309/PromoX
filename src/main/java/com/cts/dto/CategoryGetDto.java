package com.cts.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryGetDto {
    private Long categoryId;
    private String categoryName;
}

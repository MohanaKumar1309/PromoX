package com.cts.dto;


import java.math.BigDecimal;

import com.cts.enums.ReferenceType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsSummaryGetDto {
    private ReferenceType referenceType;
    private long totalEvents;
    private BigDecimal totalAmount;
}

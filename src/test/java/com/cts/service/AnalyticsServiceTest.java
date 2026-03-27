package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.cts.entity.Analytics;
import com.cts.enums.ReferenceType;
import com.cts.repository.AnalyticsRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void summary_ShouldAggregateAmounts() {
        Analytics promotion = new Analytics();
        promotion.setAmount(BigDecimal.valueOf(100));

        Analytics campaign = new Analytics();
        campaign.setAmount(BigDecimal.valueOf(50));

        when(analyticsRepository.findByReferenceType(ReferenceType.PROMOTION)).thenReturn(List.of(promotion));
        when(analyticsRepository.findByReferenceType(ReferenceType.CAMPAIGN)).thenReturn(List.of(campaign));
        when(analyticsRepository.findByReferenceType(ReferenceType.COUPON)).thenReturn(List.of());

        var summary = analyticsService.summary();

        assertEquals(3, summary.size());
        assertEquals(BigDecimal.valueOf(100), summary.get(0).getTotalAmount());
    }
}
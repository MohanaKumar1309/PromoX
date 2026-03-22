package com.cts.service;


import com.cts.dto.AnalyticsSummaryGetDto;
import com.cts.entity.Analytics;
import com.cts.entity.Customer;
import com.cts.enums.EventType;
import com.cts.enums.ReferenceType;
import com.cts.repository.AnalyticsRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public void logRedeem(Customer customer, ReferenceType type, Long referenceId, BigDecimal amount) {
        Analytics analytics = new Analytics();
        analytics.setCustomer(customer);
        analytics.setEventType(EventType.REDEEM);
        analytics.setReferenceType(type);
        analytics.setReferenceId(referenceId);
        analytics.setAmount(amount);
        analyticsRepository.save(analytics);
    }

    public List<AnalyticsSummaryGetDto> summary() {
        return Arrays.stream(ReferenceType.values())
                .map(type -> {
                    List<Analytics> rows = analyticsRepository.findByReferenceType(type);
                    BigDecimal totalAmount = rows.stream()
                            .map(Analytics::getAmount)
                            .filter(amount -> amount != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new AnalyticsSummaryGetDto(type, rows.size(), totalAmount);
                })
                .toList();
    }
}

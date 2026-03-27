package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.cts.entity.Campaign;
import com.cts.enums.ApprovalStatus;
import com.cts.repository.CampaignCategoryRepository;
import com.cts.repository.CampaignProductRepository;
import com.cts.repository.CampaignRepository;
import com.cts.repository.CategoryRepository;
import com.cts.repository.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private CampaignProductRepository campaignProductRepository;
    @Mock
    private CampaignCategoryRepository campaignCategoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CampaignService campaignService;

    @Test
    void activeByAge_ShouldFilterBySegment() {
        Campaign first = new Campaign();
        first.setMinAge(10);
        first.setMaxAge(20);
        first.setStatus(ApprovalStatus.APPROVED);
        first.setCampaignId(1L);

        Campaign second = new Campaign();
        second.setMinAge(21);
        second.setMaxAge(30);
        second.setStatus(ApprovalStatus.APPROVED);
        second.setCampaignId(2L);

        LocalDate today = LocalDate.now();
        when(campaignRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                ApprovalStatus.APPROVED, today, today)).thenReturn(List.of(first, second));
        when(campaignProductRepository.findByCampaign_CampaignId(1L)).thenReturn(List.of());
        when(campaignCategoryRepository.findByCampaign_CampaignId(1L)).thenReturn(List.of());

        List<?> result = campaignService.activeByAge(18);
        assertEquals(1, result.size());
    }
}
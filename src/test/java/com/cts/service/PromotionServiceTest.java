package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cts.dto.CreatePromotionRequest;
import com.cts.entity.Promotion;
import com.cts.enums.DiscountType;
import com.cts.repository.CategoryRepository;
import com.cts.repository.ProductRepository;
import com.cts.repository.PromotionCategoryRepository;
import com.cts.repository.PromotionProductRepository;
import com.cts.repository.PromotionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionProductRepository promotionProductRepository;
    @Mock
    private PromotionCategoryRepository promotionCategoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    void create_ShouldPersistAndNotifyStoreManager() {
        CreatePromotionRequest request = new CreatePromotionRequest();
        request.setName("Weekend Offer");
        request.setDiscountType(DiscountType.PERCENTAGE);
        request.setDiscountValue(BigDecimal.valueOf(10));
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));
        request.setType("PRODUCT");
        request.setProductIds(List.of());
        request.setCategoryIds(List.of());

        Promotion saved = new Promotion();
        saved.setPromotionId(1L);
        saved.setName("Weekend Offer");

        when(promotionRepository.save(any(Promotion.class))).thenReturn(saved);
        when(promotionProductRepository.findByPromotion_PromotionId(1L)).thenReturn(List.of());
        when(promotionCategoryRepository.findByPromotion_PromotionId(1L)).thenReturn(List.of());

        var result = promotionService.create(request, 99L);

        assertEquals(1L, result.getPromotionId());
        verify(notificationService).notifyStoreManagers(any(), any(), any());
    }
}
package com.cts.service;

import com.cts.dto.CreateProductRequest;
import com.cts.entity.Category;
import com.cts.entity.Product;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionProductRepository promotionProductRepository;
    @Mock
    private PromotionCategoryRepository promotionCategoryRepository;
    @Mock
    private CampaignProductRepository campaignProductRepository;
    @Mock
    private CampaignCategoryRepository campaignCategoryRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void createProduct_ShouldThrowWhenCategoryMissing() {
        CreateProductRequest request = new CreateProductRequest();
        request.setCategoryId(99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> catalogService.createProduct(request, 1L));
    }

    @Test
    void createProduct_ShouldSaveProduct() {
        CreateProductRequest request = new CreateProductRequest();
        request.setCategoryId(1L);
        request.setName("Product A");
        request.setSku("PA-1");
        request.setPrice(BigDecimal.TEN);

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Category A");

        Product saved = new Product();
        saved.setProductId(10L);
        saved.setCategory(category);
        saved.setName("Product A");
        saved.setSku("PA-1");
        saved.setPrice(BigDecimal.TEN);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        var result = catalogService.createProduct(request, 1L);

        assertEquals(10L, result.getProductId());
    }
}

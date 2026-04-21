package com.cts.service;


import com.cts.dto.CategoryGetDto;
import com.cts.dto.CreateCategoryRequest;
import com.cts.dto.CreateProductRequest;
import com.cts.dto.ProductGetDto;
import com.cts.entity.Category;
import com.cts.entity.Product;
import com.cts.exception.BusinessException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionCategoryRepository promotionCategoryRepository;
    private final CampaignProductRepository campaignProductRepository;
    private final CampaignCategoryRepository campaignCategoryRepository;
    private final AuditLogService auditLogService;

    public CategoryGetDto createCategory(CreateCategoryRequest request, Long actorUserId) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        Category saved = categoryRepository.save(category);
        auditLogService.logAction(actorUserId, "CATEGORY_CREATE", "Created categoryId=" + saved.getCategoryId());
        return toCategoryDto(saved);
    }

    public ProductGetDto createProduct(CreateProductRequest request, Long actorUserId) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);

        Product saved = productRepository.save(product);
        auditLogService.logAction(actorUserId, "PRODUCT_CREATE", "Created productId=" + saved.getProductId());
        return toProductDto(saved);
    }

    public List<ProductGetDto> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword == null ? "" : keyword)
                .stream()
                .map(this::toProductDto)
                .toList();
    }

    public List<CategoryGetDto> searchCategories(String keyword) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword == null ? "" : keyword)
                .stream()
                .map(this::toCategoryDto)
                .toList();
    }

    public ProductGetDto updateProduct(Long productId, CreateProductRequest request, Long actorUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setCategory(category);
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());

        Product saved = productRepository.save(product);
        auditLogService.logAction(actorUserId, "PRODUCT_UPDATE", "Updated productId=" + saved.getProductId());
        return toProductDto(saved);
    }

    public void deleteProduct(Long productId, Long actorUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        promotionProductRepository.deleteByProduct_ProductId(productId);
        campaignProductRepository.deleteByProduct_ProductId(productId);
        auditLogService.logAction(actorUserId, "PRODUCT_DELETE", "Deleted productId=" + product.getProductId() + ", sku=" + product.getSku());
        productRepository.delete(product);
    }

    public void deleteCategory(Long categoryId, Long actorUserId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!productRepository.findByCategory_CategoryId(categoryId).isEmpty()) {
            throw new BusinessException("Delete products in this category before deleting the category");
        }
        promotionCategoryRepository.deleteByCategory_CategoryId(categoryId);
        campaignCategoryRepository.deleteByCategory_CategoryId(categoryId);
        auditLogService.logAction(actorUserId, "CATEGORY_DELETE", "Deleted categoryId=" + category.getCategoryId());
        categoryRepository.delete(category);
    }

    private CategoryGetDto toCategoryDto(Category category) {
        return CategoryGetDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }

    private ProductGetDto toProductDto(Product product) {
        return ProductGetDto.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}

package com.cts.controller;

import com.cts.common.ApiResponse;
import com.cts.dto.CategoryGetDto;
import com.cts.dto.CreateCategoryRequest;
import com.cts.dto.CreateProductRequest;
import com.cts.dto.ProductGetDto;
import com.cts.security.AuthContextService;
import com.cts.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;
    private final AuthContextService authContextService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryGetDto>> createCategory(@Valid @RequestBody CreateCategoryRequest request,
                                                                      Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<CategoryGetDto>builder()
                .success(true)
                .message("Category created")
                .data(catalogService.createCategory(request, actorUserId))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductGetDto>> createProduct(@Valid @RequestBody CreateProductRequest request,
                                                                    Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<ProductGetDto>builder()
                .success(true)
                .message("Product created")
                .data(catalogService.createProduct(request, actorUserId))
                .build());
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductGetDto>>> searchProducts(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.<List<ProductGetDto>>builder()
                .success(true)
                .message("Products fetched")
                .data(catalogService.searchProducts(q))
                .build());
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryGetDto>>> searchCategories(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.<List<CategoryGetDto>>builder()
                .success(true)
                .message("Categories fetched")
                .data(catalogService.searchCategories(q))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductGetDto>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductRequest request,
            Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<ProductGetDto>builder()
                .success(true)
                .message("Product updated")
                .data(catalogService.updateProduct(productId, request, actorUserId))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId, Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        catalogService.deleteProduct(productId, actorUserId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Product deleted")
                .data(null)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId, Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        catalogService.deleteCategory(categoryId, actorUserId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted")
                .data(null)
                .build());
    }
}

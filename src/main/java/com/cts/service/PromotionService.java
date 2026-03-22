package com.cts.service;

import com.cts.dto.CreatePromotionRequest;
import com.cts.dto.PromotionGetDto;
import com.cts.entity.Category;
import com.cts.entity.Product;
import com.cts.entity.Promotion;
import com.cts.entity.PromotionCategory;
import com.cts.entity.PromotionProduct;
import com.cts.enums.ApprovalStatus;
import com.cts.enums.ReferenceType;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CategoryRepository;
import com.cts.repository.ProductRepository;
import com.cts.repository.PromotionCategoryRepository;
import com.cts.repository.PromotionProductRepository;
import com.cts.repository.PromotionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionCategoryRepository promotionCategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public PromotionGetDto create(CreatePromotionRequest request, Long createdBy) {
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMinAmount(request.getMinAmount());
        promotion.setMinQuantity(request.getMinQuantity());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setType(request.getType());
        promotion.setStatus(ApprovalStatus.PENDING);
        promotion.setCreatedBy(createdBy);
        Promotion saved = promotionRepository.save(promotion);

        if (request.getProductIds() != null) {
            for (Long productId : request.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                PromotionProduct link = new PromotionProduct();
                link.setPromotion(saved);
                link.setProduct(product);
                promotionProductRepository.save(link);
            }
        }

        if (request.getCategoryIds() != null) {
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
                PromotionCategory link = new PromotionCategory();
                link.setPromotion(saved);
                link.setCategory(category);
                promotionCategoryRepository.save(link);
            }
        }

        notificationService.notifyStoreManagers("Promotion pending approval: " + saved.getName(), ReferenceType.PROMOTION, createdBy);
        auditLogService.logAction(createdBy, "PROMOTION_CREATE", "Created promotionId=" + saved.getPromotionId());
        return toDto(saved);
    }

    public PromotionGetDto approveOrReject(Long promotionId, ApprovalStatus status, Long actorUserId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        promotion.setStatus(status);
        Promotion saved = promotionRepository.save(promotion);
        auditLogService.logAction(actorUserId, "PROMOTION_STATUS_UPDATE", "promotionId=" + saved.getPromotionId() + ", status=" + status);
        return toDto(saved);
    }

    public List<PromotionGetDto> activePromotions() {
        return getActivePromotionEntities().stream().map(this::toDto).toList();
    }

    public List<Promotion> getActivePromotionEntities() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                ApprovalStatus.APPROVED, today, today);
    }

    public List<PromotionGetDto> all() {
        return promotionRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void delete(Long promotionId, Long actorUserId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        promotionProductRepository.deleteByPromotion_PromotionId(promotionId);
        promotionCategoryRepository.deleteByPromotion_PromotionId(promotionId);
        auditLogService.logAction(actorUserId, "PROMOTION_DELETE", "Deleted promotionId=" + promotion.getPromotionId());
        promotionRepository.delete(promotion);
    }

    private PromotionGetDto toDto(Promotion promotion) {
        List<Long> productIds = promotionProductRepository.findByPromotion_PromotionId(promotion.getPromotionId())
                .stream()
                .map(link -> link.getProduct().getProductId())
                .toList();
        List<Long> categoryIds = promotionCategoryRepository.findByPromotion_PromotionId(promotion.getPromotionId())
                .stream()
                .map(link -> link.getCategory().getCategoryId())
                .toList();

        return PromotionGetDto.builder()
                .promotionId(promotion.getPromotionId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .status(promotion.getStatus())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .minAmount(promotion.getMinAmount())
                .minQuantity(promotion.getMinQuantity())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .type(promotion.getType())
                .createdBy(promotion.getCreatedBy())
                .productIds(productIds)
                .categoryIds(categoryIds)
                .build();
    }
}
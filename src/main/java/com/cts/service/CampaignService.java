package com.cts.service;

package com.cts.promoengine.service;

import com.cts.dto.CampaignGetDto;
import com.cts.dto.CreateCampaignRequest;
import com.cts.entity.Campaign;
import com.cts.entity.CampaignCategory;
import com.cts.entity.CampaignProduct;
import com.cts.entity.Category;
import com.cts.entity.Product;
import com.cts.enums.ApprovalStatus;
import com.cts.enums.ReferenceType;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CampaignCategoryRepository;
import com.cts.repository.CampaignProductRepository;
import com.cts.repository.CampaignRepository;
import com.cts.repository.CategoryRepository;
import com.cts.repository.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignProductRepository campaignProductRepository;
    private final CampaignCategoryRepository campaignCategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public CampaignGetDto create(CreateCampaignRequest request, Long createdBy) {
        Campaign campaign = new Campaign();
        campaign.setCampaignName(request.getCampaignName());
        campaign.setDescription(request.getDescription());
        campaign.setMinAge(request.getMinAge());
        campaign.setMaxAge(request.getMaxAge());
        campaign.setDiscountType(request.getDiscountType());
        campaign.setAmount(request.getAmount());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setStatus(ApprovalStatus.PENDING);
        campaign.setCreatedBy(createdBy);
        Campaign saved = campaignRepository.save(campaign);

        if (request.getProductIds() != null) {
            for (Long productId : request.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                CampaignProduct link = new CampaignProduct();
                link.setCampaign(saved);
                link.setProduct(product);
                campaignProductRepository.save(link);
            }
        }

        if (request.getCategoryIds() != null) {
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
                CampaignCategory link = new CampaignCategory();
                link.setCampaign(saved);
                link.setCategory(category);
                campaignCategoryRepository.save(link);
            }
        }

        notificationService.notifyStoreManagers("Campaign pending approval: " + saved.getCampaignName(), ReferenceType.CAMPAIGN, createdBy);
        auditLogService.logAction(createdBy, "CAMPAIGN_CREATE", "Created campaignId=" + saved.getCampaignId());
        return toDto(saved);
    }

    public CampaignGetDto approveOrReject(Long id, ApprovalStatus status, Long actorUserId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaign.setStatus(status);
        Campaign saved = campaignRepository.save(campaign);
        auditLogService.logAction(actorUserId, "CAMPAIGN_STATUS_UPDATE", "campaignId=" + saved.getCampaignId() + ", status=" + status);
        return toDto(saved);
    }

    public List<CampaignGetDto> activeByAge(Integer age) {
        return getActiveCampaignEntities(age).stream().map(this::toDto).toList();
    }

    public List<Campaign> getActiveCampaignEntities(Integer age) {
        LocalDate today = LocalDate.now();
        return campaignRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        ApprovalStatus.APPROVED, today, today)
                .stream()
                .filter(campaign -> age >= campaign.getMinAge() && age <= campaign.getMaxAge())
                .toList();
    }

    public List<CampaignGetDto> all() {
        return campaignRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void delete(Long campaignId, Long actorUserId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaignProductRepository.deleteByCampaign_CampaignId(campaignId);
        campaignCategoryRepository.deleteByCampaign_CampaignId(campaignId);
        auditLogService.logAction(actorUserId, "CAMPAIGN_DELETE", "Deleted campaignId=" + campaign.getCampaignId());
        campaignRepository.delete(campaign);
    }

    private CampaignGetDto toDto(Campaign campaign) {
        List<Long> productIds = campaignProductRepository.findByCampaign_CampaignId(campaign.getCampaignId())
                .stream()
                .map(link -> link.getProduct().getProductId())
                .toList();
        List<Long> categoryIds = campaignCategoryRepository.findByCampaign_CampaignId(campaign.getCampaignId())
                .stream()
                .map(link -> link.getCategory().getCategoryId())
                .toList();

        return CampaignGetDto.builder()
                .campaignId(campaign.getCampaignId())
                .campaignName(campaign.getCampaignName())
                .description(campaign.getDescription())
                .status(campaign.getStatus())
                .minAge(campaign.getMinAge())
                .maxAge(campaign.getMaxAge())
                .discountType(campaign.getDiscountType())
                .amount(campaign.getAmount())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .createdBy(campaign.getCreatedBy())
                .productIds(productIds)
                .categoryIds(categoryIds)
                .build();
    }
}

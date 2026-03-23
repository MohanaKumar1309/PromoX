package com.cts.service;

import com.cts.dto.CheckoutGetDto;
import com.cts.dto.CheckoutRequest;
import com.cts.entity.Campaign;
import com.cts.entity.Coupon;
import com.cts.entity.Customer;
import com.cts.entity.Order;
import com.cts.entity.Product;
import com.cts.entity.Promotion;
import com.cts.entity.Redemption;
import com.cts.enums.DiscountType;
import com.cts.enums.ReferenceType;
import com.cts.exception.BusinessException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CampaignCategoryRepository;
import com.cts.repository.CampaignProductRepository;
import com.cts.repository.OrderRepository;
import com.cts.repository.ProductRepository;
import com.cts.repository.PromotionCategoryRepository;
import com.cts.repository.PromotionProductRepository;
import com.cts.repository.RedemptionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedemptionService {

    private final ProductRepository productRepository;
    private final PromotionService promotionService;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionCategoryRepository promotionCategoryRepository;
    private final CampaignService campaignService;
    private final CampaignProductRepository campaignProductRepository;
    private final CampaignCategoryRepository campaignCategoryRepository;
    private final CouponService couponService;
    private final OrderRepository orderRepository;
    private final RedemptionRepository redemptionRepository;
    private final AnalyticsService analyticsService;

    public CheckoutGetDto checkout(Customer customer, CheckoutRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        Map<Long, Integer> qtyByProduct = new HashMap<>();
        Map<Long, Product> products = new HashMap<>();

        for (CheckoutRequest.CartLine line : request.getItems()) {
            Product product = productRepository.findById(line.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            int qty = line.getQuantity() == null || line.getQuantity() <= 0 ? 1 : line.getQuantity();
            qtyByProduct.put(product.getProductId(), qty);
            products.put(product.getProductId(), product);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        List<Long> appliedPromotions = new ArrayList<>();
        for (Promotion promotion : promotionService.getActivePromotionEntities()) {
            boolean applies = false;
            Set<Long> promotionProductIds = promotionProductRepository.findByPromotion_PromotionId(promotion.getPromotionId())
                    .stream()
                    .map(link -> link.getProduct().getProductId())
                    .collect(Collectors.toSet());
            Set<Long> promotionCategoryIds = promotionCategoryRepository.findByPromotion_PromotionId(promotion.getPromotionId())
                    .stream()
                    .map(link -> link.getCategory().getCategoryId())
                    .collect(Collectors.toSet());

            BigDecimal promotionBase = BigDecimal.ZERO;
            int totalQuantity = 0;
            for (Long productId : qtyByProduct.keySet()) {
                Product product = products.get(productId);
                if (promotionProductIds.contains(productId)
                        || promotionCategoryIds.contains(product.getCategory().getCategoryId())) {
                    applies = true;
                    int quantity = qtyByProduct.get(productId);
                    totalQuantity += quantity;
                    promotionBase = promotionBase.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                }
            }

            if (!applies) {
                continue;
            }
            if (promotion.getMinQuantity() != null && totalQuantity < promotion.getMinQuantity()) {
                continue;
            }
            if (promotion.getMinAmount() != null && promotionBase.compareTo(promotion.getMinAmount()) < 0) {
                continue;
            }

            discount = discount.add(applyDiscount(promotionBase, promotion.getDiscountType(), promotion.getDiscountValue(), null));
            appliedPromotions.add(promotion.getPromotionId());
            analyticsService.logRedeem(customer, ReferenceType.PROMOTION, promotion.getPromotionId(), promotionBase);
        }

        List<Long> appliedCampaigns = new ArrayList<>();
        for (Campaign campaign : campaignService.getActiveCampaignEntities(customer.getAge())) {
            boolean applies = false;
            Set<Long> mappedProducts = campaignProductRepository.findByCampaign_CampaignId(campaign.getCampaignId())
                    .stream()
                    .map(link -> link.getProduct().getProductId())
                    .collect(Collectors.toSet());
            Set<Long> mappedCategories = campaignCategoryRepository.findByCampaign_CampaignId(campaign.getCampaignId())
                    .stream()
                    .map(link -> link.getCategory().getCategoryId())
                    .collect(Collectors.toSet());

            for (Long productId : qtyByProduct.keySet()) {
                Product product = products.get(productId);
                if (mappedProducts.contains(productId)
                        || mappedCategories.contains(product.getCategory().getCategoryId())) {
                    applies = true;
                    break;
                }
            }

            if (applies) {
                discount = discount.add(applyDiscount(total, campaign.getDiscountType(), campaign.getAmount(), null));
                appliedCampaigns.add(campaign.getCampaignId());
                analyticsService.logRedeem(customer, ReferenceType.CAMPAIGN, campaign.getCampaignId(), total);
            }
        }

        String appliedCoupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponService.getByCode(request.getCouponCode());
            if (couponService.getActiveCouponEntities().stream().noneMatch(activeCoupon -> activeCoupon.getCouponId().equals(coupon.getCouponId()))) {
                throw new BusinessException("Coupon is not active or approved");
            }
            if (total.compareTo(coupon.getMinCartValue()) < 0) {
                throw new BusinessException("Min cart value not reached for coupon");
            }
            discount = discount.add(applyDiscount(total, coupon.getDiscountType(), coupon.getAmount(), coupon.getMaxDiscount()));
            couponService.incrementUsage(coupon);
            appliedCoupon = coupon.getCouponCode();
            analyticsService.logRedeem(customer, ReferenceType.COUPON, coupon.getCouponId(), total);
        }

        if (discount.compareTo(total) > 0) {
            discount = total;
        }

        BigDecimal finalAmount = total.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(finalAmount);
        Order savedOrder = orderRepository.save(order);

        Redemption redemption = new Redemption();
        redemption.setCustomer(customer);
        redemption.setOrder(savedOrder);
        redemption.setPromotionIds(appliedPromotions.toString());
        redemption.setCampaignIds(appliedCampaigns.toString());
        redemption.setCouponCode(appliedCoupon);
        redemption.setDiscountAmount(discount.setScale(2, RoundingMode.HALF_UP));
        redemption.setFinalAmount(finalAmount);
        redemptionRepository.save(redemption);

        return CheckoutGetDto.builder()
                .orderId(savedOrder.getOrderId())
                .totalAmount(total.setScale(2, RoundingMode.HALF_UP))
                .discountAmount(discount.setScale(2, RoundingMode.HALF_UP))
                .finalAmount(finalAmount)
                .appliedPromotions(appliedPromotions.toString())
                .appliedCampaigns(appliedCampaigns.toString())
                .appliedCoupon(appliedCoupon)
                .build();
    }

    private BigDecimal applyDiscount(BigDecimal base, DiscountType type, BigDecimal value, BigDecimal cap) {
        BigDecimal result;
        if (type == DiscountType.FLAT) {
            result = value;
        } else if (type == DiscountType.PERCENTAGE) {
            result = base.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            result = base.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }

        if (cap != null && result.compareTo(cap) > 0) {
            return cap;
        }
        return result;
    }
}
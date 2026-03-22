package com.cts.service;

import com.cts.dto.CouponGetDto;
import com.cts.dto.CreateCouponRequest;
import com.cts.entity.Coupon;
import com.cts.enums.ApprovalStatus;
import com.cts.enums.ReferenceType;
import com.cts.exception.BusinessException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public CouponGetDto create(CreateCouponRequest request, Long createdBy) {
        if (couponRepository.findByCouponCode(request.getCouponCode()).isPresent()) {
            throw new BusinessException("Coupon code already exists");
        }

        Coupon coupon = new Coupon();
        coupon.setCouponCode(request.getCouponCode());
        coupon.setCouponName(request.getCouponName());
        coupon.setDescription(request.getDescription());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setAmount(request.getAmount());
        coupon.setMinCartValue(request.getMinCartValue());
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setStatus(ApprovalStatus.PENDING);
        coupon.setCreatedBy(createdBy);

        Coupon saved = couponRepository.save(coupon);
        notificationService.notifyStoreManagers("Coupon pending approval: " + saved.getCouponCode(), ReferenceType.COUPON, createdBy);
        auditLogService.logAction(createdBy, "COUPON_CREATE", "Created couponId=" + saved.getCouponId() + ", code=" + saved.getCouponCode());
        return toDto(saved);
    }

    public CouponGetDto approveOrReject(Long id, ApprovalStatus status, Long actorUserId) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setStatus(status);
        Coupon saved = couponRepository.save(coupon);
        auditLogService.logAction(actorUserId, "COUPON_STATUS_UPDATE", "couponId=" + saved.getCouponId() + ", status=" + status);
        return toDto(saved);
    }

    public Coupon getByCode(String code) {
        return couponRepository.findByCouponCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
    }

    public void incrementUsage(Coupon coupon) {
        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new BusinessException("Coupon usage limit exceeded");
        }
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);
    }

    public List<CouponGetDto> activeCoupons() {
        LocalDate today = LocalDate.now();
        return couponRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        ApprovalStatus.APPROVED, today, today)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<Coupon> getActiveCouponEntities() {
        LocalDate today = LocalDate.now();
        return couponRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                ApprovalStatus.APPROVED, today, today);
    }

    public List<CouponGetDto> all() {
        return couponRepository.findAll().stream().map(this::toDto).toList();
    }

    public void delete(Long couponId, Long actorUserId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        auditLogService.logAction(actorUserId, "COUPON_DELETE", "Deleted couponId=" + coupon.getCouponId() + ", code=" + coupon.getCouponCode());
        couponRepository.delete(coupon);
    }

    private CouponGetDto toDto(Coupon coupon) {
        return CouponGetDto.builder()
                .couponId(coupon.getCouponId())
                .couponCode(coupon.getCouponCode())
                .couponName(coupon.getCouponName())
                .description(coupon.getDescription())
                .status(coupon.getStatus())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .discountType(coupon.getDiscountType())
                .amount(coupon.getAmount())
                .minCartValue(coupon.getMinCartValue())
                .maxDiscount(coupon.getMaxDiscount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .createdBy(coupon.getCreatedBy())
                .build();
    }
}

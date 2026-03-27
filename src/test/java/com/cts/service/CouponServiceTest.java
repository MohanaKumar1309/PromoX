package com.cts.service;

import com.cts.dto.CreateCouponRequest;
import com.cts.entity.Coupon;
import com.cts.enums.DiscountType;
import com.cts.exception.BusinessException;
import com.cts.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CouponService couponService;

    @Test
    void create_ShouldThrowOnDuplicateCode() {
        CreateCouponRequest request = new CreateCouponRequest();
        request.setCouponCode("SAVE20");

        when(couponRepository.findByCouponCode("SAVE20")).thenReturn(Optional.of(new Coupon()));

        assertThrows(BusinessException.class, () -> couponService.create(request, 1L));
    }

    @Test
    void incrementUsage_ShouldIncreaseCount() {
        Coupon coupon = new Coupon();
        coupon.setUsageCount(0);
        coupon.setUsageLimit(2);

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        couponService.incrementUsage(coupon);
        assertEquals(1, coupon.getUsageCount());
    }

    @Test
    void create_ShouldSaveWhenValid() {
        CreateCouponRequest request = new CreateCouponRequest();
        request.setCouponCode("SAVE20");
        request.setCouponName("Save 20");
        request.setUsageLimit(10);
        request.setDiscountType(DiscountType.FLAT);
        request.setAmount(BigDecimal.valueOf(20));
        request.setMinCartValue(BigDecimal.valueOf(100));
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(5));

        Coupon saved = new Coupon();
        saved.setCouponId(1L);
        saved.setCouponCode("SAVE20");

        when(couponRepository.findByCouponCode("SAVE20")).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenReturn(saved);

        var result = couponService.create(request, 10L);
        assertEquals(1L, result.getCouponId());
    }
}

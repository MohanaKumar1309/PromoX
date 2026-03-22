package com.cts.repository;

import com.cts.entity.Coupon;
import com.cts.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCouponCode(String couponCode);
    List<Coupon> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(ApprovalStatus status, LocalDate startDate, LocalDate endDate);
}

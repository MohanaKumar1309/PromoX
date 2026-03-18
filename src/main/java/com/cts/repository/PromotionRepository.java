package com.cts.repository;

import com.cts.promoengine.common.enums.ApprovalStatus;
import com.cts.promoengine.module3.promotion.entity.Promotion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByStatus(ApprovalStatus status);
    List<Promotion> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            ApprovalStatus status, LocalDate startDate, LocalDate endDate);
}
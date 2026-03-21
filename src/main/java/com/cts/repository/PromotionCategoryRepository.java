package com.cts.repository;

import com.cts.entity.PromotionCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PromotionCategoryRepository extends JpaRepository<PromotionCategory, Long> {
    List<PromotionCategory> findByPromotion_PromotionId(Long id);
    List<PromotionCategory> findByCategory_CategoryId(Long id);

    @Transactional
    void deleteByPromotion_PromotionId(Long id);

    @Transactional
    void deleteByCategory_CategoryId(Long id);
}
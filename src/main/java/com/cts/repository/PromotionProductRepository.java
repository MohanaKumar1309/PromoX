package com.cts.repository;

import com.cts.entity.PromotionProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {
    List<PromotionProduct> findByPromotion_PromotionId(Long id);
    List<PromotionProduct> findByProduct_ProductId(Long id);

    @Transactional
    void deleteByPromotion_PromotionId(Long id);

    @Transactional
    void deleteByProduct_ProductId(Long id);
}
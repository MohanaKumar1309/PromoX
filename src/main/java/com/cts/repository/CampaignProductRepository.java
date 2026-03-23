package com.cts.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.cts.entity.CampaignProduct;

public interface CampaignProductRepository extends JpaRepository<CampaignProduct, Long> {
    List<CampaignProduct> findByProduct_ProductId(Long productId);
    List<CampaignProduct> findByCampaign_CampaignId(Long campaignId);
    @Transactional
    void deleteByCampaign_CampaignId(Long id);

    @Transactional
    void deleteByProduct_ProductId(Long id);
}
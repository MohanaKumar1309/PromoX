package com.cts.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.CampaignCategory;

import jakarta.transaction.Transactional;



public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    List<CampaignCategory> findByCategory_CategoryId(Long categoryId);
    List<CampaignCategory> findByCampaign_CampaignId(Long campaignId);
    @Transactional
    void deleteByCampaign_CampaignId(Long id);

    @Transactional
    void deleteByCategory_CategoryId(Long id);
}
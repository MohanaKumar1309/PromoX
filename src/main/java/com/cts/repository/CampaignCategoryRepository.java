package com.cts.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.CampaignCategory;

public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    List<CampaignCategory> findByCategory_CategoryId(Long categoryId);
    List<CampaignCategory> findByCampaign_CampaignId(Long campaignId);
}
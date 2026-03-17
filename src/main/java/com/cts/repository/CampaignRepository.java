package com.cts.repository;


import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            ApprovalStatus status, LocalDate startDate, LocalDate endDate);
}
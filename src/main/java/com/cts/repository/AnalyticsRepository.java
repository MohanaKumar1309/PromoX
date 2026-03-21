package com.cts.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Analytics;
import com.cts.enums.ReferenceType;

public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    List<Analytics> findByReferenceType(ReferenceType referenceType);
}


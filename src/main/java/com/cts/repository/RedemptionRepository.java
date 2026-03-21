package com.cts.repository;

import com.cts.entity.Redemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByCustomer_CustId(Long customerId);
}

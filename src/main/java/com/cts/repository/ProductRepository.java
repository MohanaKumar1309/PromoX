package com.cts.repository;

import com.cts.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByCategory_CategoryId(Long categoryId);
}

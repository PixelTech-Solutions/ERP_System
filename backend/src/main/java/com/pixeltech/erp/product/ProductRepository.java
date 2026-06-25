package com.pixeltech.erp.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    // Products at or below this stock level — used by the low-stock dashboard
    // and (later) by the monitoring agent to flag inventory incidents.
    List<Product> findByStockQuantityLessThanEqual(Integer threshold);
}

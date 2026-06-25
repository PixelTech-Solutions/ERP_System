package com.pixeltech.erp.dashboard;

import com.pixeltech.erp.customer.CustomerRepository;
import com.pixeltech.erp.order.OrderRepository;
import com.pixeltech.erp.product.Product;
import com.pixeltech.erp.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Aggregated numbers for the React dashboard landing page.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrderRepository orders;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        BigDecimal revenue = orders.findAll().stream()
                .map(o -> o.getTotalAmount() == null ? BigDecimal.ZERO : o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lowStock = products.findAll().stream()
                .map(Product::getStockQuantity)
                .filter(q -> q <= 10)
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("customers", customers.count());
        stats.put("products", products.count());
        stats.put("orders", orders.count());
        stats.put("revenue", revenue);
        stats.put("lowStockProducts", lowStock);
        return stats;
    }
}

package com.pixeltech.erp.config;

import com.pixeltech.erp.customer.Customer;
import com.pixeltech.erp.customer.CustomerRepository;
import com.pixeltech.erp.order.Order;
import com.pixeltech.erp.order.OrderItem;
import com.pixeltech.erp.order.OrderRepository;
import com.pixeltech.erp.order.OrderStatus;
import com.pixeltech.erp.product.Product;
import com.pixeltech.erp.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds sample data on startup if the database is empty, so the app and the
 * monitoring/AI demo have realistic records to work with.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrderRepository orders;

    @Override
    public void run(String... args) {
        if (customers.count() > 0) {
            return; // already seeded
        }

        Customer alice = customers.save(Customer.builder()
                .name("Alice Fernando").email("alice@example.com")
                .phone("+94 77 123 4567").address("Colombo 03").build());
        Customer bruno = customers.save(Customer.builder()
                .name("Bruno Silva").email("bruno@example.com")
                .phone("+94 76 765 4321").address("Kandy").build());
        customers.save(Customer.builder()
                .name("Chen Wei").email("chen@example.com")
                .phone("+94 71 222 3333").address("Galle").build());

        Product laptop = products.save(Product.builder()
                .sku("HW-LAP-001").name("PixelBook Pro 14").category("Hardware")
                .description("14-inch developer laptop, 32GB RAM")
                .price(new BigDecimal("2499.00")).stockQuantity(25).build());
        Product mouse = products.save(Product.builder()
                .sku("HW-MOU-002").name("Ergo Wireless Mouse").category("Accessories")
                .description("Silent ergonomic mouse")
                .price(new BigDecimal("39.90")).stockQuantity(8).build());
        products.save(Product.builder()
                .sku("SW-LIC-003").name("ERP Suite License (1yr)").category("Software")
                .description("Annual subscription, per seat")
                .price(new BigDecimal("199.00")).stockQuantity(500).build());
        products.save(Product.builder()
                .sku("HW-MON-004").name("27-inch 4K Monitor").category("Hardware")
                .description("Color-accurate IPS panel")
                .price(new BigDecimal("549.00")).stockQuantity(5).build());

        Order order = Order.builder()
                .customerId(alice.getId()).customerName(alice.getName())
                .status(OrderStatus.CONFIRMED).totalAmount(BigDecimal.ZERO).build();
        OrderItem item1 = OrderItem.builder()
                .productId(laptop.getId()).productName(laptop.getName())
                .quantity(1).unitPrice(laptop.getPrice()).subtotal(laptop.getPrice()).build();
        OrderItem item2 = OrderItem.builder()
                .productId(mouse.getId()).productName(mouse.getName())
                .quantity(2).unitPrice(mouse.getPrice())
                .subtotal(mouse.getPrice().multiply(BigDecimal.valueOf(2))).build();
        order.addItem(item1);
        order.addItem(item2);
        order.setTotalAmount(item1.getSubtotal().add(item2.getSubtotal()));
        orders.save(order);

        // reflect the seeded order in stock
        laptop.setStockQuantity(laptop.getStockQuantity() - 1);
        mouse.setStockQuantity(mouse.getStockQuantity() - 2);
        products.saveAll(List.of(laptop, mouse));
    }
}

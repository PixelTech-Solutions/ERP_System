package com.pixeltech.erp.order;

import com.pixeltech.erp.common.ResourceNotFoundException;
import com.pixeltech.erp.customer.Customer;
import com.pixeltech.erp.customer.CustomerService;
import com.pixeltech.erp.product.Product;
import com.pixeltech.erp.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerService customerService;
    private final ProductService productService;

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + id + " not found"));
    }

    /**
     * Places an order: validates the customer, reserves stock for each product,
     * computes totals, and persists everything in one transaction. If any line
     * fails (e.g. insufficient stock) the whole order rolls back.
     */
    @Transactional
    public Order placeOrder(OrderRequest request) {
        Customer customer = customerService.findById(request.getCustomerId());

        Order order = Order.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.Line line : request.getItems()) {
            Product product = productService.findById(line.getProductId());
            productService.decrementStock(product, line.getQuantity());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(line.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(line.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            order.addItem(item);
        }

        order.setTotalAmount(total);
        return repository.save(order);
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return repository.save(order);
    }
}

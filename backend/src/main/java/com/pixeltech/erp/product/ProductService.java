package com.pixeltech.erp.product;

import com.pixeltech.erp.common.BusinessException;
import com.pixeltech.erp.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final ProductRepository repository;

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found"));
    }

    public List<Product> findLowStock() {
        return repository.findByStockQuantityLessThanEqual(LOW_STOCK_THRESHOLD);
    }

    @Transactional
    public Product create(ProductRequest request) {
        if (repository.existsBySku(request.getSku())) {
            throw new BusinessException("A product with SKU " + request.getSku() + " already exists");
        }
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
        return repository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return repository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product " + id + " not found");
        }
        repository.deleteById(id);
    }

    /**
     * Decrements stock when an order is placed. Throws if there isn't enough.
     * Called by the order module inside a transaction.
     */
    @Transactional
    public void decrementStock(Product product, int quantity) {
        int remaining = product.getStockQuantity() - quantity;
        if (remaining < 0) {
            throw new BusinessException(
                    "Not enough stock for " + product.getName()
                            + " (have " + product.getStockQuantity() + ", need " + quantity + ")");
        }
        product.setStockQuantity(remaining);
        repository.save(product);
    }
}

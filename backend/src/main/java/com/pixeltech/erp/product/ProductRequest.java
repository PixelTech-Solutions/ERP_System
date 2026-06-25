package com.pixeltech.erp.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "sku is required")
    private String sku;

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    private String category;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "stockQuantity is required")
    @Min(value = 0, message = "stockQuantity cannot be negative")
    private Integer stockQuantity;
}

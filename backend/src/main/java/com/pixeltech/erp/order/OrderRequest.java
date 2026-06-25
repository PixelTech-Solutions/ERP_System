package com.pixeltech.erp.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotEmpty(message = "an order needs at least one line item")
    @Valid
    private List<Line> items;

    @Data
    public static class Line {
        @NotNull(message = "productId is required")
        private Long productId;

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be at least 1")
        private Integer quantity;
    }
}

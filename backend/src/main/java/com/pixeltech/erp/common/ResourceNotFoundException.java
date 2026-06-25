package com.pixeltech.erp.common;

/**
 * Thrown when an entity (customer, product, order) is not found.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

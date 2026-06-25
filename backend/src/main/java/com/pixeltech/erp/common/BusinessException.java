package com.pixeltech.erp.common;

/**
 * Thrown for invalid business operations, e.g. ordering more stock than exists.
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

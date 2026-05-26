package com.nyxn.ecommerce.domain.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, int available, int requested) {
        super(String.format(
            "Stock insuficiente para el producto ID %d. Disponible: %d, Solicitado: %d",
            productId, available, requested
        ));
    }
}

package com.gentle.store.shopping.cart.service.exception;

import lombok.Getter;

@Getter
public class InvalidArgumentException extends RuntimeException {
    private final int quantity;

    public InvalidArgumentException(final int quantity) {
        super(STR."\{quantity} ist ungültig!");
        this.quantity = quantity;
    }
}

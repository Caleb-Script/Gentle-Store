package com.gentle.store.shopping.cart.transfer.response;

public record OrderResponse(
        String message,
        boolean isCartCorrect
) {
}

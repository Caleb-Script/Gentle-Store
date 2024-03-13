package com.gentle.store.customer.transfer.response;

public record OrderResponse(
        String message,
        boolean isCartCorrect
) {
}


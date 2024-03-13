package com.gentle.store.shopping.cart.transfer.response;

import java.math.BigDecimal;

public record InventoryResponse(
        String name,
        BigDecimal price
) {
}

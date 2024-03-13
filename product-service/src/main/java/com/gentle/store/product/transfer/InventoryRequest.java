package com.gentle.store.product.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryRequest(
        String skuCode,
        Integer quantity,
        BigDecimal unitPrice,
        UUID productId
) {
}

package com.gentle.store.inventory.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link com.gentle.store.inventory.entity.Inventory}
 */
public record InventoryDTO(
        String skuCode,
        Integer quantity,
        BigDecimal unitPrice,
        UUID productId
) {
}
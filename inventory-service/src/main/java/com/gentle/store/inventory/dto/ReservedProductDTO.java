package com.gentle.store.inventory.dto;

import java.util.UUID;

/**
 * DTO for {@link com.gentle.store.inventory.entity.ReservedProduct}
 */
public record ReservedProductDTO(
        int quantity,
        UUID customerId,
        String skuCode
) {
}
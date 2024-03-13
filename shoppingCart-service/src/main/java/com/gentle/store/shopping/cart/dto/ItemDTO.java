package com.gentle.store.shopping.cart.dto;

import com.gentle.store.shopping.cart.entity.Item;

/**
 * DTO for {@link Item}
 */
public record ItemDTO(
        String skuCode,
        int quantity
) {
}
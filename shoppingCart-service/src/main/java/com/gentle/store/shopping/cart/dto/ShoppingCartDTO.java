package com.gentle.store.shopping.cart.dto;

import com.gentle.store.shopping.cart.entity.ShoppingCart;

import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link ShoppingCart}
 */
public record ShoppingCartDTO(
        List<ItemDTO> cartItems,
        UUID customerId
) {
}
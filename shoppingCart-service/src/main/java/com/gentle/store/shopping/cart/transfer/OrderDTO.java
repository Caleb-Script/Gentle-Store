package com.gentle.store.shopping.cart.transfer;

import com.gentle.store.shopping.cart.dto.ItemDTO;

import java.util.List;

public record OrderDTO(
        String orderNumber,
        List<ItemDTO> orderLineItems
) {
}

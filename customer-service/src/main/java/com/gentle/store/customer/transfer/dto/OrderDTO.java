package com.gentle.store.customer.transfer.dto;

import com.gentle.store.customer.transfer.ItemDTO;

import java.util.List;

public record OrderDTO(
        String orderNumber,
        List<ItemDTO> orderedItems
) {
}
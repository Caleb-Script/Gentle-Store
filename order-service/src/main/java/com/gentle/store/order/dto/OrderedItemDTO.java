package com.gentle.store.order.dto;

import com.gentle.store.order.entity.OrderedItem;

import java.math.BigDecimal;

/**
 * DTO for {@link OrderedItem}
 */
public record OrderedItemDTO(
    String skuCode,
    Integer quantity,
    BigDecimal price
) {
}
package com.gentle.store.order.dto;

import com.gentle.store.order.entity.Order;

import java.util.List;

/**
 * DTO for {@link Order}
 */

public record OrderDTO(
    String orderNumber,
    List<OrderedItemDTO> orderedItems
) {
}
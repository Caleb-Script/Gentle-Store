package com.gentle.store.customer.transfer.dto;

import java.math.BigDecimal;

public record OrderedItemDTO(
        String skuCode,
        Integer quantity
) {
}

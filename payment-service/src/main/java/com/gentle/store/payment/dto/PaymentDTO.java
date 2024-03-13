package com.gentle.store.payment.dto;

import java.math.BigDecimal;

/**
 * DTO for {@link com.gentle.store.payment.entity.Payment}
 */
public record PaymentDTO(
        BigDecimal totalAmount,
        String orderNumber
) {
}
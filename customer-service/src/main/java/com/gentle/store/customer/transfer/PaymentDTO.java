package com.gentle.store.customer.transfer;

import java.math.BigDecimal;

public record PaymentDTO(String orderNumber, BigDecimal totalAmount) {
}

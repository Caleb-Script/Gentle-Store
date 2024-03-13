package com.gentle.store.customer.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(UUID destination, BigDecimal amount) {
}

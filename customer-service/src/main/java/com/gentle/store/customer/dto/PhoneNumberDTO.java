package com.gentle.store.customer.dto;

import com.gentle.store.customer.entity.PhoneNumber;

/**
 * DTO for {@link PhoneNumber}
 */
public record PhoneNumberDTO(
        String dialingCode,
        String number,
        Boolean isDefaultPhoneNumber
) {
}
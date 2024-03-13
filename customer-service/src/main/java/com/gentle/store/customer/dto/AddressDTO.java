package com.gentle.store.customer.dto;

import com.gentle.store.customer.entity.Address;
import com.gentle.store.customer.entity.enums.StateType;

/**
 * DTO for {@link Address}
 */
public record AddressDTO(
        String street,
        String houseNumber,
        String zipCode,
        StateType state,
        String city
) {
}
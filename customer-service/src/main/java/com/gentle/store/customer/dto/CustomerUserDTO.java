package com.gentle.store.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerUserDTO(
    @JsonProperty("customer")
    CustomerDTO customerDTO,

    @JsonProperty("user")
    CustomUserDTO userDTO
) {
}

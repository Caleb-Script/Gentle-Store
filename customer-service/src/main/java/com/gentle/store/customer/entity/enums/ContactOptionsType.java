package com.gentle.store.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ContactOptionsType {
    EMAIL("Email"),
    PHONE("Phone"),
    MAIL("Mail"),
    SMS("SMS"),
    OTHER("Other");

    private final String  type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static ContactOptionsType of(final String value) {
        return Stream.of(values())
                .filter(interest -> interest.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}

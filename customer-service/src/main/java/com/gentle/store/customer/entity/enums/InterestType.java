package com.gentle.store.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum InterestType {
    INVESTMENTS("Investments"),
    SAVING_AND_FINANCE("Saving and Finance"),
    CREDIT_AND_DEBT("Credit and Debt"),
    BANK_PRODUCTS_AND_SERVICES("Bank Products and Services"),
    FINANCIAL_EDUCATION_AND_ADVICE("Financial Education and Advice"),
    REAL_ESTATE("Real Estate"),
    INSURANCE("Insurance"),
    SUSTAINABLE_FINANCE("Sustainable Finance"),
    TECHNOLOGY_AND_INNOVATION("Technology and Innovation"),
    TRAVEL("Travel");

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static InterestType of(final String value) {
        return Stream.of(values())
                .filter(interest -> interest.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}


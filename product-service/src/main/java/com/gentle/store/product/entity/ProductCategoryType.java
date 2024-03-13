package com.gentle.store.product.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ProductCategoryType {
    OBST_UND_GEMUESE("OG"),

    /** Weibliches Geschlecht. */
    ELEKTRONIK("E"),

    /** Diverser Geschlechtstyp. */
    HAUSHALT("H");

    /** Die Zeichenfolgen repräsentation des Geschlechtstyps. */
    private final String type;

    @JsonCreator
    public static ProductCategoryType of(final String value) {
        return Stream.of(values())
                .filter(productCategory -> productCategory.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String getType() {
        return type;
    }
}

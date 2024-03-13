package com.gentle.store.inventory.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum InventoryStatusType {

    DISCONTINUED("D"),
    AVAILABLE("A"),

    /** Weibliches Geschlecht. */
    RESERVED("R"),

    /** Diverser Geschlechtstyp. */
    OUT_OF_STOCK("O");

    /** Die Zeichenfolgen repräsentation des Geschlechtstyps. */
    private final String type;

    @JsonCreator
    public static InventoryStatusType of(final String value) {
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

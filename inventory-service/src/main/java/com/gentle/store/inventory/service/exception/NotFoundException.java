package com.gentle.store.inventory.service.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RuntimeException, falls kein Customer gefunden wurde.
 */
@Getter
public final class NotFoundException extends RuntimeException {

    private final UUID id;
    private final Map<String, List<String>> suchkriterien;
    private final String skuCode;

    public NotFoundException(final String skuCode) {
        super(STR."Kein Inventar mit dem Sku-Code: \{skuCode} gefunden!");
        this.skuCode = skuCode;
        suchkriterien = null;
        id = null;
    }

    public NotFoundException(final UUID id) {
        super(STR."Kein Inventar mit der ID: \{id} gefunden!");
        this.id = id;
        suchkriterien = null;
        skuCode = null;
    }

    public NotFoundException(final UUID customerID, final UUID id) {
        super(STR."Keine Resavierung für den Kunden mit der ID: \{customerID} gefunden!");
        this.id = customerID;
        suchkriterien = null;
        skuCode = null;
    }

    public NotFoundException(final Map<String, List<String>> searchCriteria) {
        super("Keine Inventare gefunden mit diesen Suchkriterien gefunden!");
        id = null;
        this.suchkriterien = searchCriteria;
        skuCode = null;
    }

    public NotFoundException() {
        super("Keine Inventare gefunden.");
        id = null;
        suchkriterien = null;
        skuCode = null;
    }
}

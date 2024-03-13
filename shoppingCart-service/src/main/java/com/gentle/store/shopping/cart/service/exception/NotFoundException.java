package com.gentle.store.shopping.cart.service.exception;

import com.gentle.store.shopping.cart.entity.Item;
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
    private final Item item;

    public NotFoundException(final UUID id) {
        super(STR."Keinen Warenkorb mit der ID \{id} gefunden.");
        this.id = id;
        item = null;
        suchkriterien = null;
    }

    public NotFoundException(final Item item) {
        super(STR."Ihr Warenkorb beeinhaltet nicht das Produkt [\{item.getName()}]");
        id = null;
        this.item = item;
        suchkriterien = null;
    }

    public NotFoundException(final UUID id, final UUID id2) {
        super(STR."Keinen Warenkorb mit der KundenID \{id} gefunden.");
        this.id = id;
        item = null;
        suchkriterien = null;
    }

    public NotFoundException(final Map<String, List<String>> searchCriteria) {
        super("Keinen Warenkorb gefunden mit diesen Suchkriterien gefunden.");
        id = null;
        item = null;
        this.suchkriterien = searchCriteria;
    }

    public NotFoundException() {
        super("Keinen Warenkorb gefunden.");
        id = null;
        item = null;
        suchkriterien = null;
    }
}

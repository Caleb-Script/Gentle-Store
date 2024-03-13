package com.gentle.store.inventory.service.exception;

import com.gentle.store.inventory.entity.Inventory;
import com.gentle.store.inventory.transfer.ItemDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class InsufficientInventoryException extends RuntimeException {

    private final Inventory inventory;
    private final ItemDTO itemDTO;
    private final int free;

    public InsufficientInventoryException(Inventory inventory, int free, ItemDTO itemDTO) {

        super(STR."vom Produkt \{inventory.getProductName()} gibt es nur noch \{free} und nicht die gewünschten \{itemDTO.quantity()}");
        this.inventory = inventory;
        this.itemDTO = itemDTO;
        this.free = free;
        log.error(STR."vom Produkt \{inventory.getProductName()} gibt es nur noch \{inventory.getQuantity()} und nicht die gewünschten \{itemDTO.quantity()}");
    }

    public InsufficientInventoryException(Inventory inventory) {
        super(STR."Sie haben nicht soviel vom Produkt \{inventory.getProductName()} Reserviert");
        this.inventory = inventory;
        itemDTO = null;
        free = 0;
    }
}

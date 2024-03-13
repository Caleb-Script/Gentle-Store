package com.gentle.store.shopping.cart.service.exception;

import com.gentle.store.shopping.cart.entity.Item;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class InsufficientQuantityException extends RuntimeException {
    private final Item item;

    public InsufficientQuantityException(Item item) {

        super(STR."vom Produkt \{item.getName()} [\{item.getSkuCode()}]haben sie nur \{item.getQuantity()} im Warenkorb");
        this.item = item;
        log.error(STR."vom Produkt \{item.getName()} haben sie nur \{item.getQuantity()} im Warenkorb");
    }

}

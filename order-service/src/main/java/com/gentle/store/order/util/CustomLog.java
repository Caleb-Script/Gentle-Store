package com.gentle.store.order.util;

import com.gentle.store.order.entity.Order;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLog {

    public static void logOrderItems(Order order, String message) {
        order.getOrderedItems().forEach(item -> {
            log.info("{} Activity: id={}, skuCode={}, quantity={}, price={}",
                    message,
                    item.getId() != null ? item.getId():"null",
                    item.getSkuCode(),
                    item.getQuantity(),
                    item.getPrice());
        });
        log.info("ENDE");
    }
}

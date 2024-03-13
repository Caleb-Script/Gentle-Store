package com.gentle.store.shopping.cart.util;

import com.gentle.store.shopping.cart.entity.Item;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomLog {

    public static void logActivity(List<Item> customer, String kp) {
        customer.forEach(activity -> {
            log.trace("{} Activity: id={}, zeit={}, typ={}, content={}",
                    kp,
                    activity.getId() != null ? activity.getId():"null",
                    activity.getName(),
                    activity.getPrice(),
                    activity.getQuantity());
        });
        log.info("ENDE");
    }
}

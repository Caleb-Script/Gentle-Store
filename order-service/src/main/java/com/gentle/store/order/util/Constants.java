package com.gentle.store.order.util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Constants {
    public static final String INVENTORY_CLIENT = "http://inventory-service/inventory";
    public static final String ORDER_PATH = "/order";
    public static final String ITEM_GRAPH = "Order.orderLineItem";

    private static final String ADMIN_CREDENTIALS = "admin:p";
    public static final String ADMIN_BASIC_AUTH =
            STR."Basic \{new String(Base64.getEncoder().encode(ADMIN_CREDENTIALS.getBytes(ISO_8859_1)), ISO_8859_1)}";
}

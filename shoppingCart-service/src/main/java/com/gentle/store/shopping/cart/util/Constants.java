package com.gentle.store.shopping.cart.util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Constants {

    public static final String INVENTORY_CLIENT = "http://inventory-service/inventory";
    public static final String ORDER_CLIENT = "http://order-service/order";

    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_HOST = "x-forwarded-host";
    public static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";
    public static final String CUSTOMER_PREFIX = "/customer";
    public static final String PROBLEM_PATH = "/problem";
    public static final String SHOPPING_CART_PATH = "/shoppingCart";
    public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";
    private static final String ADMIN_CREDENTIALS = "admin:p";
    public static final String ADMIN_BASIC_AUTH =
            STR."Basic \{new String(Base64.getEncoder().encode(ADMIN_CREDENTIALS.getBytes(ISO_8859_1)), ISO_8859_1)}";

    public static final String ITEM_GRAPH = "ShoppingCart.items";


    public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";

}

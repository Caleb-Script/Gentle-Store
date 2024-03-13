package com.gentle.store.inventory.util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Constants {
    public static final String PRODUCT_CLIENT = "http://product-service/product";



    private static final String ADMIN_CREDENTIALS = "admin:p";
    public static final String ADMIN_BASIC_AUTH =
            STR."Basic \{new String(Base64.getEncoder().encode(ADMIN_CREDENTIALS.getBytes(ISO_8859_1)), ISO_8859_1)}";
    public static final String RESERVED_PRODUCTS_GRAPH = "Inventory.reservedProductsList";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_HOST = "x-forwarded-host";
    public static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";
    public static final String Inventory_PREFIX = "/inventory";
    public static final String INVENTORY_PATH = "/inventory";
    public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";
    public static final String PROBLEM_PATH = "/problem";
    public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";
}

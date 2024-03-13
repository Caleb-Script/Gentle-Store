package com.gentle.store.customer.util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Constants {

    public static final String SHOPPING_CART_CLIENT = "http://shoppingCart-service/shoppingCart";
    public static final String ORDER_CLIENT = "http://order-service/order";
    public static final String PAYMENT_CLIENT = "http://payment-service/payment";


    public static final String PROBLEM_PATH = "/problem";
    public static final String CUSTOMER_PATH = "/customer";
    public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";
    public static final String SURNAME_PATH = "/surname";
    private static final String ADMIN_CREDENTIALS = "admin:p";
    public static final String ADMIN_BASIC_AUTH =
            STR."Basic \{new String(Base64.getEncoder().encode(ADMIN_CREDENTIALS.getBytes(ISO_8859_1)), ISO_8859_1)}";

    public static final String ADDRESS_GRAPH = "Customer.address";
    public static final String ALL_GRAPH = "Customer.addressActivities";


    public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";
    public static final String SURNAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
    public static final String FIRST_NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
    public static final String USERNAME_PATTERN = "[a-zA-Z0-9_\\-.]{4,}";

    public static final long MIN_CATEGORY = 1L;
    public static final long MAX_CATEGORY = 6L;
    public static final int NAME_MAX_LENGTH = 40;
    public static final int EMAIL_MAX_LENGTH = 40;
    public static final int HOMEPAGE_MAX_LENGTH = 40;
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final String ZIP_CODE_PATTERN = "^\\d{5}$";
    public static final String GERMAN_STREET_PATTERN = "^[a-zA-ZäöüßÄÖÜ\\s]+(?:\\s\\d+)?$";
    public static final int STREET_MAX_LENGTH = 100;
    public static final int HOUSE_NUMBER_MAX_LENGTH = 5;
    public static final int ZIP_CODE_MAX_LENGTH = 5;
    public static final int STATE_MAX_LENGTH = 20;
    public static final int CITY_MAX_LENGTH = 50;
    public static final int CONTENT_MAX_LENGTH = 1000;
}

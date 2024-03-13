package com.gentle.store.customer.security;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    CUSTOMER("KUNDE"),
    ACTUATOR("ACTUATOR");
    public static final String ROLE_PREFIX = "ROLE_";
    private final String value;
}

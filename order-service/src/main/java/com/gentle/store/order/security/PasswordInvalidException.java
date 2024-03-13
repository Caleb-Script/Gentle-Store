package com.gentle.store.order.security;

import lombok.Getter;

@Getter
public class PasswordInvalidException extends RuntimeException {
    private final String password;

    PasswordInvalidException(final String password) {
        super(STR."Ungueltiges Passwort \{password}");
        this.password = password;
    }
}

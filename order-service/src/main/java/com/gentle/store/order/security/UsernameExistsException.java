package com.gentle.store.order.security;

import lombok.Getter;

@Getter
public class UsernameExistsException extends RuntimeException {
    private final String username;

    UsernameExistsException(final String username) {
        super(STR."Der Benutzername \{username} existiert bereits.");
        this.username = username;
    }
}

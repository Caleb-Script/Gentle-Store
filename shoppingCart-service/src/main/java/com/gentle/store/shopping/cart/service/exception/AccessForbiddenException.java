package com.gentle.store.shopping.cart.service.exception;


import com.gentle.store.shopping.cart.security.Role;
import lombok.Getter;

import java.util.Collection;

@Getter
public class AccessForbiddenException extends RuntimeException {
    /**
     * Vorhandene Rollen.
     */
    private final Collection<Role> roles;

    public AccessForbiddenException(final Collection<Role> roles) {
        super(STR."Unzureichende Rollen: \{roles}");
        this.roles = roles;
    }
}

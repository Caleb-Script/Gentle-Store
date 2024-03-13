package com.gentle.store.inventory.security;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@ToString(callSuper = true)
public class CustomUser extends User {
    /**
     * Konstruktor.
     *
     * @param username Benutzername
     * @param password Passwort
     * @param authorities Rollen bzw. Authorities gemäß Spring Security
     */
    public CustomUser(
        final String username,
        final String password,
        final Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
    }
}

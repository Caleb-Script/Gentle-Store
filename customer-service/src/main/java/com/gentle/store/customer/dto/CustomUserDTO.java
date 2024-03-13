package com.gentle.store.customer.dto;

import com.gentle.store.customer.security.CustomUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static com.gentle.store.customer.security.Role.CUSTOMER;
import static com.gentle.store.customer.security.Role.ROLE_PREFIX;


public record CustomUserDTO(String username, String password) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns.
     *
     * @return Objekt für den Anwendungskern
     */
    public UserDetails toUserDetails() {
        return new CustomUser(username, password, List.of(new SimpleGrantedAuthority(STR."\{ROLE_PREFIX}\{CUSTOMER}")));
    }
}

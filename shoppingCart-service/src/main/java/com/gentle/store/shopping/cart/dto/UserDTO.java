package com.gentle.store.shopping.cart.dto;

import com.gentle.store.shopping.cart.security.CustomUser;
import com.gentle.store.shopping.cart.security.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public record UserDTO(String username, String password) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns.
     *
     * @return Objekt für den Anwendungskern
     */
    public UserDetails toUserDetails() {
        return new CustomUser(username, password, List.of(new SimpleGrantedAuthority(STR."\{Role.ROLE_PREFIX}\{Role.CUSTOMER}")));
    }
}

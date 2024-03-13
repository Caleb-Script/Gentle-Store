package com.gentle.store.customer.security;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Entity
@Table(name = "login")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Login {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    private String username;

    private String password;

    @Transient
    private List<Role> roles;

    @Column(name = "roles")
    private String rolesString;

    UserDetails toUserDetails() {
        final List<SimpleGrantedAuthority> authorities = roles == null || roles.isEmpty()
            ? emptyList()
            : roles.stream()
                .map(rolle -> new SimpleGrantedAuthority(STR."\{Role.ROLE_PREFIX}\{rolle}"))
                .toList();
        return new CustomUser(username, password, authorities);
    }

    @PrePersist
    private void buildRollenStr() {
        final var rollenStrList = roles.stream().map(Enum::name).toList();
        rolesString = String.join(",", rollenStrList);
    }

    @PostLoad
    private void loadRollen() {
        final var rollenArray = rolesString.split(",");
        roles = Arrays.stream(rollenArray).map(Role::valueOf).toList();
    }
}

package com.gentle.store.shopping.cart.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static java.util.Locale.GERMAN;

@Service
@RequiredArgsConstructor
@Slf4j

public class CustomUserDetailsService implements UserDetailsService {
    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern NUMBERS = Pattern.compile(".*\\d.*");
    private static final Pattern SYMBOLS = Pattern.compile(".*[!-/:-@\\[-`{-\\~].*");

    private final LoginRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        log.debug("loadUserByUsername: {}", username);
        if (username == null || username.isEmpty()) {
            //noinspection ReturnOfNull
            return null;
        }
        final var loginOpt = repo.findByUsername(username);
        if (loginOpt.isEmpty()) {
            //noinspection ReturnOfNull
            return null;
        }
        final var userDetails = loginOpt.get().toUserDetails();
        log.debug("loadUserByUsername: {}", userDetails);
        return userDetails;
    }

    public Login save(final UserDetails user) {
        log.debug("save: {}", user);
        final var login = userDetailsToLogin(user);
        repo.save(login);
        return login;
    }

    private Login userDetailsToLogin(final UserDetails user) {
        log.debug("userDetailsToLogin: {}", user);

        final var password = user.getPassword();
        if (!checkPassword(password)) {
            throw new PasswordInvalidException(password);
        }

        final var username = user.getUsername();
        final var isUsernameExisting = repo.existsByUsername(username);
        if (isUsernameExisting) {
            throw new UsernameExistsException(username);
        }

        // Die Account-Informationen des Kunden transformieren: in Account-Informationen fuer die Security-Komponente
        final var login = new Login();
        login.setUsername(username.toLowerCase(GERMAN));

        final var encodedPassword = passwordEncoder.encode(password);
        login.setPassword(encodedPassword);

        final var rollen = user.getAuthorities()
            .stream()
            .map(grantedAuthority -> {
                final var rolleStr = grantedAuthority
                    .getAuthority()
                    .substring(Role.ROLE_PREFIX.length());
                return Role.valueOf(rolleStr);
            })
            .toList();
        login.setRoles(rollen);

        log.trace("userDetailsToLogin: login = {}", login);
        return login;
    }

    private boolean checkPassword(final CharSequence password) {
        if (password.length() < MIN_LENGTH) {
            log.error("password hat nur {} zeichen mindestens müssen es aber {} zeichen sein",password.length(),MIN_LENGTH);
            return false;
        }
        if (!UPPERCASE.matcher(password).matches()) {
            log.error("Password muss einen großbuchstaben haben!");
            return false;
        }
        if (!LOWERCASE.matcher(password).matches()) {
            log.error("Password muss einen kleinbuchstaben haben!");
            return false;
        }
        if (!NUMBERS.matcher(password).matches()) {
            log.error("Password muss einen nummer haben!");
            return false;
        }
        if (!SYMBOLS.matcher(password).matches()) {
            log.error("Password muss einen Sonderzeichen haben!");
            return false;
        }
        return SYMBOLS.matcher(password).matches();
    }
}

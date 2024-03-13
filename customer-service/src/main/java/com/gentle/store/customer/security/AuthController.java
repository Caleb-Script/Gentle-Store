package com.gentle.store.customer.security;

import com.gentle.store.customer.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Controller
@RequestMapping(AuthController.AUTH_PATH)
@Tag(name = "Authentifizierung API")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    /**
     * Pfad für Authentifizierung.
     */
    public static final String AUTH_PATH = "/auth";

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * "Einloggen" bei Basic Authentication.
     *
     * @param userDTO Benutzerkennung und Passwort.
     * @return Response mit der Collection der Rollen oder Statuscode 401.
     */
    @PostMapping(path = "login", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einloggen bei Basic Authentifizierung", tags = "Auth")
    @ApiResponse(responseCode = "200", description = "Eingeloggt")
    @ApiResponse(responseCode = "401", description = "Fehler bei Username oder Passwort")
    ResponseEntity<Collection<Role>> login(final UserDTO userDTO) {
        log.debug("login: {}", userDTO);
        final var username = userDTO.username();
        final var password = userDTO.password();

        final var userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return status(UNAUTHORIZED).build();
        }
        final var user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return status(UNAUTHORIZED).build();
        }

        final var roles = user.getRoles();
        log.debug("login: roles={}", roles);
        return ok(roles);
    }
    private Optional<Login> findByUsername(final String username) {
        return loginRepository.findByUsername(username);
    }

    /**
     * Die Rollen zur eigenen Benutzerkennung ermitteln.
     *
     * @param principal Benutzerkennung als Objekt zum Interface Principal.
     * @return Response mit den eigenen Rollen oder Statuscode 401, falls man nicht eingeloggt ist.
     */
    @GetMapping(path = "/roles", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Abfrage der eigenen Rollen", tags = "Auth")
    @ApiResponse(responseCode = "200", description = "Rollen ermittelt")
    @ApiResponse(responseCode = "401", description = "Fehler bei Authentifizierung")
    ResponseEntity<Collection<Role>> findOwnRoles(final Principal principal) {
        if (principal == null) {
            return status(UNAUTHORIZED).build();
        }

        final var username = principal.getName();
        log.debug("findOwnRoles: username={}", username);

        final var userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return status(UNAUTHORIZED).build();
        }
        final var user = userOpt.get();

        final var roles = user.getRoles();
        log.debug("findOwnRoles: roles={}", roles);
        return ok(roles);
    }
}

package com.gentle.store.customer.rest;

import com.gentle.store.customer.dto.CustomerUpdateDTO;
import com.gentle.store.customer.security.PasswordInvalidException;
import com.gentle.store.customer.security.UsernameExistsException;
import com.gentle.store.customer.service.CustomerReadService;
import com.gentle.store.customer.service.CustomerWriteService;
import com.gentle.store.customer.service.ProblemType;
import com.gentle.store.customer.service.exception.ConstraintViolationsException;
import com.gentle.store.customer.service.exception.EmailExistsException;
import com.gentle.store.customer.service.exception.VersionInvalidException;
import com.gentle.store.customer.service.exception.VersionOutdatedException;
import com.gentle.store.customer.service.patch.InvalidPatchOperationException;
import com.gentle.store.customer.service.patch.PatchOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.customer.util.Constants.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.status;
@RestController
@RequestMapping(CUSTOMER_PATH)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteController {
    /**
     * Basispfad für "type" innerhalb von ProblemDetail.
     */
    private final CustomerWriteService customerWriteService;
    private final CustomerReadService customerReadService;

    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen Kunden mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Customer nicht vorhanden")
    @ApiResponse(responseCode = "412", description = "Versionsnummer falsch")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    @ApiResponse(responseCode = "428", description = VERSION_NUMBER_MISSING)
    ResponseEntity<Void> put(
            @PathVariable final UUID id,
            @RequestBody final CustomerUpdateDTO customerUpdateDTO,
            @RequestHeader("If-Match") final Optional<String> version,
            final HttpServletRequest request
    ) {
        final var updatedCustomer = customerWriteService.put(id, customerUpdateDTO, version, request);
        log.debug("put: updatedCustomer={}", updatedCustomer);
        return noContent().eTag(STR."\"\{updatedCustomer.getVersion()}\"").build();
    }

    @PatchMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen Kunden mit einzelnen neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Customer nicht vorhanden")
    @ApiResponse(responseCode = "412", description = "Versionsnummer falsch")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    @ApiResponse(responseCode = "428", description = VERSION_NUMBER_MISSING)
    ResponseEntity<Void> patch(
            @PathVariable final UUID id,
            @RequestBody final Collection<PatchOperation> operations,
            @RequestHeader("If-Match") final Optional<String> version,
            final Authentication authentication,
            final HttpServletRequest request
    ) {
        final var user = (UserDetails) authentication.getPrincipal();
        log.debug("PATCH: id={}, version={}, operations={}, user={}", id, version, operations, user);

        if (user == null) {
            log.error("Trotz Spring Security wurde patch() ohne Benutzerkennung aufgerufen");
            return status(FORBIDDEN).build();
        }
        final var updatedCustomer = customerWriteService.patch (id, operations, version, request);
        log.debug("PATCH: updatedCustomer={}", updatedCustomer);
        return noContent().eTag(STR."\"\{updatedCustomer.getVersion()}\"").build();
    }

    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Kunden anhand der ID loeschen", tags = "Loeschen")
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    String deleteById(@PathVariable final UUID id) {
        log.debug("deleteById: id={}", id);
        return customerWriteService.deleteById(id);
    }

    @ExceptionHandler
    ProblemDetail onConstraintViolations(
            final ConstraintViolationsException ex,
            final HttpServletRequest request
    ) {
        log.error("onConstraintViolations: {}", ex.getMessage());

        final var customerViolations = ex.getViolations()
                .stream()
                .map(violation -> {
                    final var path = violation.getPropertyPath();
                    final var msg = violation.getMessage();
                    final var annot = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
                    return STR."\{path}: \{msg} (\{annot})";
                })
                .toList();
        log.error("onConstraintViolations: {}", customerViolations);
        // [ und ] aus dem String der Liste entfernen
        final var violationsStr = customerViolations.toString();
        final var detail = violationsStr.substring(1, violationsStr.length() - 2);

        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));

        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onEmailExists(final EmailExistsException ex, final HttpServletRequest request) {
        log.error("onEmailExists: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onUsernameExists(
            final UsernameExistsException ex,
            final HttpServletRequest request
    ) {
        log.error("onUsernameExists: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onPasswordInvalid(
            final PasswordInvalidException ex,
            final HttpServletRequest request
    ) {
        log.error("onPasswordInvalid: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onVersionOutdated(
            final VersionOutdatedException ex,
            final HttpServletRequest request
    ) {
        log.error("onVersionOutdated: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(PRECONDITION_FAILED, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onVersionInvalidException(
            final VersionInvalidException ex,
            final HttpServletRequest request
    ) {
        log.error("onVersionInvalidException: {}", ex.getMessage());
        final var detail = ex.getMessage().split(",")[4].split("=")[1];
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onInvalidPatchOperation(
            final InvalidPatchOperationException ex,
            final HttpServletRequest request
    ) {
        log.error("onMessageNotReadable: {}", ex.getMessage());
        final var detail = ex.getMessage().split(",")[4].split("=")[1];
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.UNPROCESSABLE.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpServletRequest request
    ) {
        log.error("onMessageNotReadable: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.BAD_REQUEST.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }
}

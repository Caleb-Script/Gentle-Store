package com.gentle.store.customer.rest;

import com.gentle.store.customer.dto.CustomerActivityDTO;
import com.gentle.store.customer.dto.CustomerUpdateDTO;
import com.gentle.store.customer.dto.CustomerUserDTO;
import com.gentle.store.customer.mapper.CustomerMapper;
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
import com.gentle.store.customer.transfer.ItemDTO;
import com.gentle.store.customer.util.UriHelper;
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
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.customer.entity.enums.CustomerStatusType.ACTIVE;
import static com.gentle.store.customer.util.Constants.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping(CUSTOMER_PATH)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteController {
    private final CustomerWriteService customerWriteService;
    private final CustomerReadService customerReadService;
    private final CustomerMapper customerMapper;
    private final UriHelper uriHelper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neuen Kunden anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Customer neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> addCustomer(@RequestBody final CustomerUserDTO customerUserDTO, final HttpServletRequest request) throws URISyntaxException {
        log.debug("addCustomer: customerUserDTO={}", customerUserDTO);

        final var customerDTO = customerUserDTO.customerDTO();
        final var userDTO = customerUserDTO.userDTO();

        if (customerDTO == null || userDTO == null) {
            log.error("Es fehlen daten!");
            return badRequest().build();
        }

        final var customer = customerWriteService.create(customerDTO, userDTO.toUserDetails());
        final var baseUri = uriHelper.getBaseUri(request);
        final var location = new URI(STR."\{baseUri.toString()}/\{customer.getId()}");

        log.debug("addCustomer: new Customer={}", customer);
        log.info("addCustomer: new CustomerId={}", customer.getId());
        return created(location).build();
    }

    @PostMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Eine Kunden Aktivität erstellen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Aktivität neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> addActivity(
            @RequestBody final CustomerActivityDTO customerActivityDTO,
            @PathVariable final UUID id,
            Authentication authentication,
            final HttpServletRequest request
    ) {
        log.debug("addActivity: customerActivityDTO={}", customerActivityDTO);

        final var user = (UserDetails) authentication.getPrincipal();
        final var customerDb = customerReadService.findById(id, user, false);
        final var customerDTO = customerMapper.toCustomerUpdateDTO(customerDb);
        customerDTO.activities().add(customerActivityDTO);
        final var customer = customerMapper.toCustomer(customerDTO);
        customer.setStatus(ACTIVE);
        final var updatedCustomer = customerWriteService.update(customer, id, customerDb.getVersion());

        log.debug("addActivity: new customerActivity={}", updatedCustomer.getActivities().getLast());
        return noContent().eTag(STR."\"\{updatedCustomer.getVersion()}\"").build();
    }

    @PostMapping("add/{id}")
    String addItems(@PathVariable final UUID id, @RequestBody final List<ItemDTO> itemDTOs) {
        log.debug("addItems: id={}, itemDTO={}", id, itemDTOs);

        return customerWriteService.addItem(id, itemDTOs);
    }

    @PostMapping(value = "remove/{id}", consumes = APPLICATION_JSON_VALUE)
    String removeItems(@PathVariable final UUID id, @RequestBody final List<ItemDTO> itemDTOs) {
        log.debug("removeItems: id={}, itemDTO={}", id, itemDTOs);

        return customerWriteService.removeItems(id,itemDTOs);
    }

    @PostMapping("placeOrder/{id}")
    String placeOrder(@RequestBody List<ItemDTO> itemDTOs, @PathVariable final UUID id) {
        log.debug("addItems: id={}, itemDTO={}", id, itemDTOs);

        return customerWriteService.placeOrder(itemDTOs,id);
    }

    @PostMapping("pay/{customerId}/{orderNumber}")
    String pay(@PathVariable final UUID customerId, @PathVariable String orderNumber) {
        log.info("pay: customerId={}, orderNumber={}", customerId, orderNumber);

        return customerWriteService.makePayment(orderNumber, customerId);
    }

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
    @Operation(summary = "Einen Kunden anhand der ID löschen", tags = "Löschen")
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

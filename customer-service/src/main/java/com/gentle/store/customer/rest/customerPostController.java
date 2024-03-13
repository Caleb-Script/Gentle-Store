package com.gentle.store.customer.rest;

import com.gentle.store.customer.dto.CustomerActivityDTO;
import com.gentle.store.customer.dto.CustomerUserDTO;
import com.gentle.store.customer.mapper.CustomerMapper;
import com.gentle.store.customer.service.CustomerReadService;
import com.gentle.store.customer.service.CustomerWriteService;
import com.gentle.store.customer.transfer.ItemDTO;
import com.gentle.store.customer.transfer.TransactionDTO;
import com.gentle.store.customer.util.UriHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static com.gentle.store.customer.entity.enums.CustomerStatusType.ACTIVE;
import static com.gentle.store.customer.util.Constants.CUSTOMER_PATH;
import static com.gentle.store.customer.util.Constants.ID_PATTERN;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(CUSTOMER_PATH)
@RequiredArgsConstructor
@Slf4j
public class customerPostController {
    private final CustomerWriteService customerWriteService;
    private final CustomerReadService customerReadService;
    private final CustomerMapper customerMapper;
    private final UriHelper uriHelper;
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neuen Kunden anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Customer neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> post(
            @RequestBody final CustomerUserDTO customerUserDTO,
            final HttpServletRequest request
    ) throws URISyntaxException {
        log.debug("POST: customerUserDTO={}", customerUserDTO);

        final var customerDTO = customerUserDTO.customerDTO();
        final var userDTO = customerUserDTO.userDTO();

        if (customerDTO == null || userDTO == null) {
            log.error("Es fehlen daten!");
            return badRequest().build();
        }

        final var customer = customerWriteService.create(customerDTO, userDTO.toUserDetails());
        final var baseUri = uriHelper.getBaseUri(request);
        final var location = new URI(STR."\{baseUri.toString()}/\{customer.getId()}");

        log.debug("POST: new Customer={}", customer);
        log.info("POST: new CustomerId={}", customer.getId());
        return created(location).build();
    }

    @PostMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Eine Kunden Aktivität erstellen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Aktivität neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> post(
            @RequestBody final CustomerActivityDTO customerActivityDTO,
            @PathVariable final UUID id,
            Authentication authentication,
            final HttpServletRequest request
    ) {
        log.debug("POST: customerActivityDTO={}", customerActivityDTO);

        final var user = (UserDetails) authentication.getPrincipal();
        final var customerDb = customerReadService.findById(id, user, false);
        final var customerDTO = customerMapper.toCustomerUpdateDTO(customerDb);
        customerDTO.activities().add(customerActivityDTO);
        final var customer = customerMapper.toCustomer(customerDTO);
        customer.setStatus(ACTIVE);
        final var updatedCustomer = customerWriteService.update(customer, id, customerDb.getVersion());

        log.debug("POST new customerActivity: {}", updatedCustomer.getActivities().getLast());
        return noContent().eTag(STR."\"\{updatedCustomer.getVersion()}\"").build();
    }

    @PostMapping("add/{id}")
    String addItems(@PathVariable final UUID id, @RequestBody final List<ItemDTO> orderedItemDTOs) {
        return customerWriteService.addItem(id, orderedItemDTOs);
    }

    @PostMapping(value = "remove/{id}", consumes = APPLICATION_JSON_VALUE)
    String removeItems(@PathVariable final UUID id, @RequestBody final List<ItemDTO> itemsDTOs) {
        return customerWriteService.removeItems(id,itemsDTOs);
    }

    @PostMapping("placeOrder/{id}")
    String placeOrder(@RequestBody List<ItemDTO> itemDTO, @PathVariable final UUID id) {
        return customerWriteService.placeOrder(itemDTO,id);
    }

    @PostMapping("pay/{customerId}/{orderNumber}")
    String pay(@PathVariable final UUID customerId, @PathVariable String orderNumber) {
        log.info("pay: customerId={}, orderNumber={}", customerId, orderNumber);
        return customerWriteService.makePayment(orderNumber, customerId);
    }
}

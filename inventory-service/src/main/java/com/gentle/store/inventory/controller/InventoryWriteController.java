package com.gentle.store.inventory.controller;

import com.gentle.store.inventory.dto.InventoryDTO;
import com.gentle.store.inventory.service.InventoryWriteService;
import com.gentle.store.inventory.service.ProblemType;
import com.gentle.store.inventory.service.exception.InsufficientInventoryException;
import com.gentle.store.inventory.transfer.ItemDTO;
import com.gentle.store.inventory.util.UriHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.inventory.util.Constants.ID_PATTERN;
import static com.gentle.store.inventory.util.Constants.PROBLEM_PATH;
import static com.gentle.store.inventory.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryWriteController {
    private final InventoryWriteService inventoryWriteService;
    private final UriHelper uriHelper;


    @PostMapping(value = "reserve/{customerId}",consumes = APPLICATION_JSON_VALUE)
    String reserveItems(@RequestBody List<ItemDTO> itemDTOs, @PathVariable final UUID customerId) {
        final var message = inventoryWriteService.reserveItems(itemDTOs,customerId);
        //final var baseUri = uriHelper.getBaseUri(request);
        //final var location = new URI(STR."\{baseUri.toString()}/\{customerId}");
        //return created(location).body(message);
        return message;
    }

    @PostMapping("buy/{customerId}")
    String buyItems(@PathVariable final UUID customerId,@RequestBody List<ItemDTO> itemDTOs) {
        log.info("buyItems: orderNumber={}, orderedItems={}", customerId, itemDTOs);
        inventoryWriteService.buyItems(customerId,itemDTOs);
        return "hat geklappt";
    }


    @PostMapping
    String addInventory(
            @RequestBody InventoryDTO inventoryDTO,
            final HttpServletRequest request
    ) throws URISyntaxException {
//        if(inventoryDTO == null)
//            return badRequest().build();

        final var inventory = inventoryWriteService.create(inventoryDTO);
        final var baseUri = uriHelper.getBaseUri(request);
        final var location = new URI(STR."\{baseUri.toString()}/\{inventory.getId()}");
        log.debug("POST: new Inventory={}", inventory);
        log.info("POST: new InventoryId={}", inventory.getId());
        return STR."Produkt: \{inventory.getProductId()} wurde hinzugefügt";
    }

    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> put(
            @PathVariable final UUID id,
            @RequestBody final InventoryDTO inventoryDTO,
            @RequestHeader("If-Match") final Optional<String> version,
            final HttpServletRequest request
    ) {
        log.debug("put: id={}, inventoryDTO={}", id, inventoryDTO);

        final int versionInt = getVersion(version, request);
        final var updatedInventory = inventoryWriteService.update(inventoryDTO, id, versionInt);

        log.debug("put: updatedInventory={}", updatedInventory);
        return noContent().eTag(STR."\"\{updatedInventory.getVersion()}\"").build();
    }

    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    ResponseEntity<String> deleteById(@PathVariable final UUID id) {
        log.debug("deleteById: id={}", id);
        return ok().body(inventoryWriteService.deleteById(id));
    }

    @ExceptionHandler
    ProblemDetail onInsufficientInventory(
            final InsufficientInventoryException ex,
            final HttpServletRequest request
    ) {
        log.error("oonInsufficientInventor: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONFLICT.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }
}

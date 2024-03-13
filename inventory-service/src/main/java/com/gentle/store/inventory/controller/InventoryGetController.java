package com.gentle.store.inventory.controller;

import com.gentle.store.inventory.entity.Inventory;
import com.gentle.store.inventory.model.InStockResponse;
import com.gentle.store.inventory.model.InventoryModel;
import com.gentle.store.inventory.model.InventoryResponse;
import com.gentle.store.inventory.service.InventoryReadService;
import com.gentle.store.inventory.util.UriHelper;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryGetController {
    private final InventoryReadService inventoryReadService;
    private final ObservationRegistry observationRegistry;
    private final UriHelper uriHelper;

    @GetMapping("/skuCode")
    @ResponseStatus(HttpStatus.OK)
    public List<InStockResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryReadService.isInStock(skuCode);
    }

    @GetMapping("skuCode/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse getBySkuCode(@PathVariable String skuCode) {
        return inventoryReadService.findBySkuCode(skuCode);
    }

    @GetMapping(path= "{id}", produces = HAL_JSON_VALUE)
    ResponseEntity<InventoryModel> getById(
            @PathVariable UUID id,
            @RequestHeader("If-None-Match") final Optional<String> version,
            final HttpServletRequest request
    ) {
        log.debug("getById: id={}",id);

        final var product = Observation
                .createNotStarted("find-by-id", observationRegistry)
                .observe(() -> inventoryReadService.findById(id,false));
        log.debug("getById: {}", product);

        assert product != null;
        final var currentVersion = STR."\"\{product.getVersion()}\"";
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }
        final var model = inventoryToModel(product, request);

        log.debug("getById: response={}", model);
        return ok().eTag(currentVersion).body(model);
    }

    private InventoryModel inventoryToModel(final Inventory inventory, final HttpServletRequest request) {
        final var model = new InventoryModel(inventory);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = STR."\{baseUri}/\{inventory.getId()}";

        final var selfLink = Link.of(idUri);
        final var listLink = Link.of(baseUri, LinkRelation.of("list"));
        final var addLink = Link.of(baseUri, LinkRelation.of("add"));
        final var updateLink = Link.of(idUri, LinkRelation.of("update"));
        final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
        model.add(selfLink, listLink, addLink, updateLink, removeLink);
        return model;
    }

    @GetMapping
    CollectionModel<InventoryModel> get(
            @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
            final HttpServletRequest request
    ) {
        log.debug("get: searchCriteria={}", searchCriteria);

        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var models = inventoryReadService.find(searchCriteria)
                .stream()
                .map(customer -> {
                    final var model = new InventoryModel(customer);
                    model.add(Link.of(STR."\{baseUri}/\{customer.getId()}"));
                    return model;
                })
                .toList();

        log.debug("get: models={}", models);
        return CollectionModel.of(models);
    }

    @GetMapping(path= "/reserved/{id}", produces = HAL_JSON_VALUE)
    ResponseEntity<Inventory> getByIdAll(
            @PathVariable UUID id,
            @RequestHeader("If-None-Match") final Optional<String> version,
            final HttpServletRequest request
    ) {
        log.debug("getById: id={}",id);

        final var inventory = Observation
                .createNotStarted("find-by-id", observationRegistry)
                .observe(() -> inventoryReadService.findById(id,true));
        assert inventory != null;
        log.debug("getById: inventory={}, reservedProducts={}", inventory,inventory.getReservedProductsList());

        final var currentVersion = STR."\"\{inventory.getVersion()}\"";
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }
        final var model = inventoryToModel(inventory, request);

        log.debug("getById: response={}", model);
        return ok().eTag(currentVersion).body(inventory);
    }
}

package com.gentle.store.product.controller;

import com.gentle.store.product.entity.Product;
import com.gentle.store.product.model.ProductModel;
import com.gentle.store.product.service.ProductReadService;
import com.gentle.store.product.util.UriHelper;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.product.util.Constants.PRODUCT_PATH;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(PRODUCT_PATH)
@RequiredArgsConstructor
@Slf4j
public class ProductGetController {
    private final ProductReadService productReadService;
    private final ObservationRegistry observationRegistry;
    private final UriHelper uriHelper;

    @GetMapping(path= "/{id}", produces = HAL_JSON_VALUE)
    ResponseEntity<ProductModel> getById(
            @PathVariable UUID id,
            @RequestHeader("If-None-Match") final Optional<String> version,
            final HttpServletRequest request
    ) {
        log.debug("getById: id={}",id);

        final var product = Observation
                .createNotStarted("find-by-id", observationRegistry)
                .observe(() -> productReadService.findById(id));
        log.debug("getById: {}", product);

        assert product != null;
        final var currentVersion = STR."\"\{product.getVersion()}\"";
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }
        final var model = productToModel(product, request);

        log.debug("getById: response={}", model);
        return ok().eTag(currentVersion).body(model);
    }

    private ProductModel productToModel(final Product product, final HttpServletRequest request) {
        final var model = new ProductModel(product);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = STR."\{baseUri}/\{product.getId()}";

        final var selfLink = Link.of(idUri);
        final var listLink = Link.of(baseUri, LinkRelation.of("list"));
        final var addLink = Link.of(baseUri, LinkRelation.of("add"));
        final var updateLink = Link.of(idUri, LinkRelation.of("update"));
        final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
        model.add(selfLink, listLink, addLink, updateLink, removeLink);
        return model;
    }

    @GetMapping
    CollectionModel<ProductModel> get(
            @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
                            final HttpServletRequest request
    ) {
        log.debug("get: searchCriteria={}", searchCriteria);

        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var models = productReadService.find(searchCriteria)
                .stream()
                .map(customer -> {
                    final var model = new ProductModel(customer);
                    model.add(Link.of(STR."\{baseUri}/\{customer.getId()}"));
                    return model;
                })
                .toList();

        log.debug("get: models={}", models);
        return CollectionModel.of(models);
    }
}

package com.gentle.store.shopping.cart.controller;

import com.gentle.store.shopping.cart.dto.ItemDTO;
import com.gentle.store.shopping.cart.entity.ShoppingCart;
import com.gentle.store.shopping.cart.response.ShoppingCartModel;
import com.gentle.store.shopping.cart.service.CartReadService;
import com.gentle.store.shopping.cart.transfer.response.OrderResponse;
import com.gentle.store.shopping.cart.util.UriHelper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.shopping.cart.util.Constants.ID_PATTERN;
import static com.gentle.store.shopping.cart.util.Constants.SHOPPING_CART_PATH;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(SHOPPING_CART_PATH)
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartGetController {
    private final CartReadService cartReadService;
    private final ObservationRegistry observationRegistry;
    private final UriHelper uriHelper;

    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    ResponseEntity<ShoppingCartModel> getById(
        @PathVariable final UUID id,
        @RequestHeader("If-None-Match") final Optional<String> version,
        final HttpServletRequest request,
        Authentication authentication
    ) {
        log.debug("getById: id={} version={}", id, version);

        final var user = (UserDetails) authentication.getPrincipal();
        log.debug("getById: user={}", user);

        // "Distributed Tracing" durch https://micrometer.io bei Aufruf eines anderen Microservice
        final var shoppingCart = Observation
            .createNotStarted("find-by-id", observationRegistry)
            .observe(() -> cartReadService.findById(id, user,false));


        assert shoppingCart != null;
        final var currentVersion = STR."\"\{shoppingCart.getVersion()}\"";
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }
        final var model = shoppingCartToModel(shoppingCart, request, true);

        log.debug("getById: response={}", model);
        return ok().eTag(currentVersion).body(model);
    }

    @GetMapping(path = "full/{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    ResponseEntity<ShoppingCart> getCompleteCart(
            @PathVariable final UUID id,
            @RequestHeader("If-None-Match") final Optional<String> version,
            Authentication authentication
    ) {
        log.debug("getById: id={} version={}", id, version);

        final var user = (UserDetails) authentication.getPrincipal();
        log.debug("getById: user={}", user);

        final var shoppingCart = cartReadService.findById(id,user, true);
        log.debug("getById: shoppingCart={}", shoppingCart);

        final var currentVersion = STR."\"\{shoppingCart.getVersion()}\"";
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }

        log.debug("getById: response={}", shoppingCart);
        return ok().eTag(currentVersion).body(shoppingCart);
    }

    private ShoppingCartModel shoppingCartToModel(final ShoppingCart shoppingCart, final HttpServletRequest request, final boolean allLinks) {
        final var model = new ShoppingCartModel(shoppingCart);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var customerUri = STR."http://localhost:8083/customer/\{shoppingCart.getCustomerId()}";
        final var idUri = STR."\{baseUri}/\{shoppingCart.getId()}";

        final var customerLink = Link.of(customerUri, LinkRelation.of("customer"));
        final var selfLink = Link.of(idUri);

        model.add(customerLink, selfLink);

        if (allLinks) {
            final var listLink = Link.of(baseUri, LinkRelation.of("list"));
            final var addLink = Link.of(baseUri, LinkRelation.of("add"));
            final var updateLink = Link.of(idUri, LinkRelation.of("update"));
            final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
            model.add(listLink, addLink, updateLink, removeLink);
        }

        return model;
    }

    @GetMapping(produces = HAL_JSON_VALUE)
    CollectionModel<ShoppingCartModel> get(
        @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
        final HttpServletRequest request
    ) {
        log.debug("get: searchCriteria={}", searchCriteria);

        final var models = cartReadService.findAll()
            .stream()
            .map(shoppingCart -> shoppingCartToModel(shoppingCart, request, false))
            .toList();

        log.debug("get: models={}", models);
        return CollectionModel.of(models);
    }

    @GetMapping("isInCart/{customerId}")
    OrderResponse placeOrder(@RequestBody List<ItemDTO> orderLineItemsDTOsString, @PathVariable final UUID customerId, Authentication authentication) {

        final var user = (UserDetails) authentication.getPrincipal();
        return cartReadService.isInCart(orderLineItemsDTOsString, customerId, user);
    }
}

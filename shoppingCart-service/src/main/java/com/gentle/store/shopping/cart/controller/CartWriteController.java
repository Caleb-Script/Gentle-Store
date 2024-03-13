package com.gentle.store.shopping.cart.controller;

import com.gentle.store.shopping.cart.dto.ItemDTO;
import com.gentle.store.shopping.cart.dto.UserDTO;
import com.gentle.store.shopping.cart.service.CartWriteService;
import com.gentle.store.shopping.cart.service.ProblemType;
import com.gentle.store.shopping.cart.service.exception.InsufficientQuantityException;
import com.gentle.store.shopping.cart.service.exception.InvalidArgumentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.gentle.store.shopping.cart.util.Constants.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(SHOPPING_CART_PATH)
@RequiredArgsConstructor
@Slf4j
public class CartWriteController {
    private final CartWriteService cartWriteService;

    @PostMapping(path= "{customerId}")
    String CreateShoppingCart(@PathVariable final UUID customerId, @RequestBody final UserDTO userDTO) {
        log.debug("CreateShoppingCart: customerId={} userDTO={}", customerId, userDTO);

        final var user = userDTO.toUserDetails();
        final var shoppingCart = cartWriteService.create(customerId, user);

        log.debug("CreateShoppingCart: new shoppingCart={}", shoppingCart);
        return STR."Der Warenkorb mit der ID: [\{shoppingCart.getId()}] wurde erstellt";
    }

    @PostMapping(path= "add/{customerId}",consumes = APPLICATION_JSON_VALUE)
    public String addItems(@PathVariable final UUID customerId, @RequestBody final List<ItemDTO> itemDTOs, Authentication authentication)
    {
        log.debug("addItems: customerId={}", customerId);
        log.debug("addItems: itemDTOs={}", itemDTOs);

        final var user = (UserDetails) authentication.getPrincipal();
        log.debug("addItems: user={}", user);

        return cartWriteService.addItems(itemDTOs,customerId, user);
    }

    @PostMapping("remove/{customerId}")
    String removeItems(@RequestBody List<ItemDTO> orderLineItemsDTOs,@PathVariable UUID customerId, Authentication authentication) {
        final var user = (UserDetails) authentication.getPrincipal();
        return cartWriteService.removeItems(orderLineItemsDTOs, customerId, user);
    }


    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    String deleteById(@PathVariable final UUID id) {
        log.debug("deleteById: id={}", id);
        return cartWriteService.deleteByCustomerId(id);
    }

    @PostMapping("order/{customerId}")
    String placeOrder(@RequestBody List<ItemDTO> orderLineItemsDTOsString, @PathVariable final UUID customerId, Authentication authentication) {
        log.debug("placeOrder: customerId={}, items={}", customerId, orderLineItemsDTOsString);

        final var user = (UserDetails) authentication.getPrincipal();
        log.debug("placeOrder: user={}", user);

        return cartWriteService.placeOrder(orderLineItemsDTOsString, customerId, user);
    }

    @ExceptionHandler
    ProblemDetail onInvalidArgument(
            final InvalidArgumentException ex,
            final HttpServletRequest request
    ) {
        log.error("onInvalidArgument: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.BAD_REQUEST.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onInsufficientQuantity(
            final InsufficientQuantityException ex,
            final HttpServletRequest request
    ) {
        log.error("onInsufficientQuantity: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.BAD_REQUEST.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }


}

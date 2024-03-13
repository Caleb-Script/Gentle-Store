package com.gentle.store.shopping.cart.service;

import com.gentle.store.shopping.cart.dto.ItemDTO;
import com.gentle.store.shopping.cart.entity.Item;
import com.gentle.store.shopping.cart.entity.ShoppingCart;
import com.gentle.store.shopping.cart.repository.CartRepository;
import com.gentle.store.shopping.cart.security.Role;
import com.gentle.store.shopping.cart.service.exception.AccessForbiddenException;
import com.gentle.store.shopping.cart.service.exception.NotFoundException;
import com.gentle.store.shopping.cart.transfer.response.InventoryResponse;
import com.gentle.store.shopping.cart.transfer.response.OrderResponse;
import io.micrometer.observation.annotation.Observed;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.gentle.store.shopping.cart.security.Role.ADMIN;
import static com.gentle.store.shopping.cart.util.Constants.ADMIN_BASIC_AUTH;
import static com.gentle.store.shopping.cart.util.Constants.INVENTORY_CLIENT;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartReadService {
    private final CartRepository cartRepository;
    private final WebClient.Builder webClientBuilder;

    @Observed(name = "find-by-id")
    public @NonNull ShoppingCart findById(final UUID id, UserDetails user, final boolean fetchAll) {
        log.debug("findById: id={} fetchAll={}", id,fetchAll);

        final var shoppingCart = !fetchAll
                ? cartRepository.findById(id).orElseThrow(() -> new NotFoundException(id))
                : cartRepository.findByIdFetchAll(id).orElseThrow(() -> new NotFoundException(id));

        return getShoppingCart(user, fetchAll, shoppingCart);
    }

    @NonNull
    private ShoppingCart getShoppingCart(UserDetails user, boolean fetchAll, ShoppingCart shoppingCart) {
        final var fullShoppingCart = getCartFullDetails(fetchAll, shoppingCart);

        if (fullShoppingCart.getCustomerUsername().contentEquals(user.getUsername())) {
            return fullShoppingCart;
        }

        final var roles = user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.substring(Role.ROLE_PREFIX.length()))
                .map(Role::valueOf)
                .toList();
        if (!roles.contains(ADMIN)) {
            // nicht admin, aber keine eigenen (oder keine) Kundendaten
            throw new AccessForbiddenException(roles);
        }

        log.debug("getCartFullDetails: shoppingCart={}", shoppingCart);
        log.debug("getCartFullDetails; items={}", fetchAll ? shoppingCart.getCartItems() : "N/A");
        return fullShoppingCart;
    }

    public @NonNull ShoppingCart findByCustomerId(final UUID customerId, UserDetails user, final boolean fetchAll) {
        log.debug("findByCustomerId: customerId={}, fetchAll={}", customerId, fetchAll);

        final var shoppingCart = !fetchAll
                ? cartRepository.findByCustomerId(customerId).orElseThrow(() -> new NotFoundException(customerId, customerId))
                : cartRepository.findByCustomerIdFetchAll(customerId).orElseThrow(() -> new NotFoundException(customerId, customerId));

        return getShoppingCart(user, fetchAll, shoppingCart);
    }

    @NonNull
    private ShoppingCart getCartFullDetails(boolean fetchAll, ShoppingCart shoppingCart) {
        log.debug("getCartFullDetails: fetchAll={}, shoppingCart={}", fetchAll, shoppingCart);

        if (fetchAll) {
            shoppingCart.getCartItems().forEach(item -> {
                final var inventoryDetails = getInventoryDetails(item);
                log.trace("getCartFullDetails: inventoryDetails={}", inventoryDetails);
                item.setPrice(inventoryDetails.price());
                item.setName(inventoryDetails.name());
                log.trace("getCartFullDetails: completeItem={}", item);
            });
        }
        return shoppingCart;
    }


    InventoryResponse getInventoryDetails(Item item) {
        log.debug("getInventoryDetails: item={}",item);

        return webClientBuilder.build()
                .get()
                .uri(STR."\{INVENTORY_CLIENT}/skuCode/\{item.getSkuCode()}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();
    }

    public @NonNull Collection<ShoppingCart> findAll() {
        log.info("findAll");

        final var carts = cartRepository.findAll();
        log.debug("findAll: carts={}",carts);

        return carts;
    }

    public OrderResponse isInCart(List<ItemDTO> itemsDTOs, final UUID customerId, UserDetails user) {
        final var cartDb = cartRepository.findByCustomerId(customerId).orElseThrow(() -> new NotFoundException(customerId, customerId));
        log.debug("isInCart: cartDb={} username={}",cartDb,user.getUsername());
        log.debug("isInCart: items={}", cartDb.getCartItems());

        StringBuilder messageBuilder = new StringBuilder();
        final boolean[] allItemsInCart = {
                itemsDTOs.stream()
                        .allMatch(itemDTO -> {
                    boolean itemInCart = cartDb.getCartItems().stream()
                            .filter(orderedItem -> itemDTO.skuCode().equals(orderedItem.getSkuCode()))
                            .anyMatch(orderedItem -> {
                                log.trace("isInCart: item={}",itemDTO);
                                log.trace("isInCart: itemDb={}",orderedItem);
                                return itemDTO.quantity() <= orderedItem.getQuantity();});
                    if (!itemInCart) {
                        messageBuilder.append(STR."Item \{itemDTO.skuCode()} is not in the shoppingCart.\n");
                    }

                    return itemInCart;
                })};

        log.debug("isInCart: allItemsInCart={}",allItemsInCart);


        itemsDTOs.stream().filter(orderedItem -> cartDb.getCartItems().stream()
                        .anyMatch(item -> item.getSkuCode().equals(orderedItem.skuCode())))
                .forEach(item -> {
                    var optionalCartItem = cartDb.getCartItems().stream()
                            .filter(orderedItem -> orderedItem.getSkuCode().equals(item.skuCode()))
                            .findFirst();
                    if (optionalCartItem.isPresent()) {
                        if (item.quantity() > optionalCartItem.get().getQuantity()) {
                            messageBuilder.append(STR."Item \{item.skuCode()} quantity in shoppingCart is less than ordered(item quantity:\{item.quantity()} in shoppingCart \{optionalCartItem.get().getQuantity()}).\n ");
                            allItemsInCart[0] = false;
                        }
                    } else {
                        // Log an error if the item is not found in the shoppingCart
                        log.error("Item {} not found in the shoppingCart.", item.skuCode());
                        allItemsInCart[0] = false;
                    }
                });

        String message = messageBuilder.toString().trim();

        if (!allItemsInCart[0]) {
            log.warn(message);
        } else {
            log.info("All itemsDTOs are in the shoppingCart.");
        }
        if (cartDb.getCustomerUsername().equals(user.getUsername()))
            return new OrderResponse(message, allItemsInCart[0]);

        final var roles = user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.substring(Role.ROLE_PREFIX.length()))
                .map(Role::valueOf)
                .toList();
        if (!roles.contains(ADMIN)) {
            // nicht admin, aber keine eigenen (oder keine) Kundendaten
            throw new AccessForbiddenException(roles);
        }

        return new OrderResponse(message, allItemsInCart[0]);
    }
}

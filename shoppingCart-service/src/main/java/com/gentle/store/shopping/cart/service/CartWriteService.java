package com.gentle.store.shopping.cart.service;

import com.gentle.store.shopping.cart.dto.ItemDTO;
import com.gentle.store.shopping.cart.dto.ShoppingCartDTO;
import com.gentle.store.shopping.cart.entity.Item;
import com.gentle.store.shopping.cart.entity.ShoppingCart;
import com.gentle.store.shopping.cart.mapper.CartMapper;
import com.gentle.store.shopping.cart.repository.CartRepository;
import com.gentle.store.shopping.cart.security.CustomUserDetailsService;
import com.gentle.store.shopping.cart.service.exception.InsufficientQuantityException;
import com.gentle.store.shopping.cart.service.exception.InvalidArgumentException;
import com.gentle.store.shopping.cart.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static com.gentle.store.shopping.cart.util.Constants.ADMIN_BASIC_AUTH;
import static com.gentle.store.shopping.cart.util.Constants.ORDER_CLIENT;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CartWriteService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WebClient.Builder webClientBuilder;
    private final CartReadService cartReadService;
    private final CustomUserDetailsService userService;

    public ShoppingCart create(final UUID customerId, final UserDetails user) {
        log.debug("create: customerId={} username={}", customerId, user.getUsername());

        final var login = userService.save(user);
        log.trace("create: login={}", login);

        final var shoppingcart = cartMapper.toShoppingcart(new ShoppingCartDTO(new ArrayList<>(), customerId));
        shoppingcart.setTotalAmount(BigDecimal.valueOf(0));
        shoppingcart.setComplete(true);
        shoppingcart.setCustomerUsername(user.getUsername());

        log.debug("create: new ShoppingCart={}", shoppingcart);
        return cartRepository.save(shoppingcart);
    }

    public String placeOrder(final List<ItemDTO> items, final UUID customerId, UserDetails user) {
        log.debug("placeOrder: customerId={} orderDto={}", customerId, items);

        final var isInCart = cartReadService.isInCart(items, customerId, user);
        if (!isInCart.isCartCorrect()) {
            log.error("placeOrder: isInCart={}", isInCart);
            return isInCart.message();
        }
        final var message = removeItems(items, customerId, user);
        log.debug(message);
        log.trace("placeOrder: isInCart={}", isInCart);

        return webClientBuilder.build()
                .post()
                .uri(STR."\{ORDER_CLIENT}/\{customerId}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .body(BodyInserters.fromValue(items))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String addItems(List<ItemDTO> itemDTOs, final UUID customerId, UserDetails user) {
        final var shoppingCart = cartReadService.findByCustomerId(customerId, user, true);

        final List<Item> newItems = new ArrayList<>();

        itemDTOs.forEach(itemDTO -> {
            log.trace("addItems: itemDTO={}", itemDTO);

            shoppingCart.getCartItems().stream()
                    .filter(orderedItem -> {
                        if (itemDTO.quantity() < 0)
                            throw new InvalidArgumentException(itemDTO.quantity());
                        return orderedItem.getSkuCode().equals(itemDTO.skuCode());
                    })
                    .findFirst()
                    .ifPresentOrElse(existingItem -> {
                                final var bereitsVorhandeneMenge = existingItem.getQuantity();
                                existingItem.setQuantity(existingItem.getQuantity() + itemDTO.quantity());
                                final var itemCopy = new Item(existingItem);
                                itemCopy.setQuantity(existingItem.getQuantity() - bereitsVorhandeneMenge);

                                log.trace("addItems: temCopy={}", itemCopy);
                                Optional<Item> existingItemOptional = newItems.stream()
                                        .filter(item -> item.getSkuCode().equals(itemCopy.getSkuCode()))
                                        .findFirst();

                                if (existingItemOptional.isPresent()) {
                                    final var existingItem2 = existingItemOptional.get();
                                    existingItem2.setQuantity(existingItem2.getQuantity() + itemCopy.getQuantity());
                                } else {
                                    newItems.add(itemCopy);
                                }

                            },
                            () -> {
                                final var item = cartMapper.toItem(itemDTO);
                                final var inventoryDetails = cartReadService.getInventoryDetails(item);
                                log.trace("addItems: inventoryDetails={}", inventoryDetails);

                                item.setPrice(inventoryDetails.price());
                                item.setName(inventoryDetails.name());
                                log.trace("addItems: item={}", item);

                                shoppingCart.getCartItems().add(item);
                                newItems.add(item);
                            });
        });

        shoppingCart.setTotalAmount(totalAmount(shoppingCart.getCartItems()));
        log.debug("addItems: shoppingCart={}", shoppingCart);

        final List<Item> items = new ArrayList<>(shoppingCart.getCartItems());
        log.debug("addItems: items={}", items);

        shoppingCart.setComplete(false);

        cartRepository.save(shoppingCart);
        shoppingCart.addItems(items);

        IntStream.range(0, items.size()).forEach(_ -> shoppingCart.getCartItems().removeFirst());


        final StringBuilder respond = new StringBuilder();
        newItems.stream()
                .distinct()
                .forEach(newItem -> respond.append(STR."Item: \{newItem.getName()} wurde \{newItem.getQuantity()} mal  hinzugefügt für den preis von\{newItem.getPrice()}€\n"));


        respond.append(STR."\nder neue Wert des Warenkorb beträgt \{shoppingCart.getTotalAmount()}€");

        log.debug("addItems: shoppingCart={}", shoppingCart);
        log.debug("addItems: shoppingCartItems={}", shoppingCart.getCartItems());
        return respond.toString();
    }

    public String removeItems(List<ItemDTO> orderLineItemsDTOs, UUID customerId, UserDetails user) {
        log.info("removeItems: customerId={}, orderLineItemsDTOs={}", customerId, orderLineItemsDTOs);

        final var cart = cartReadService.findByCustomerId(customerId, user, true);

        List<Item> emptySkuCodes = new ArrayList<>();

        orderLineItemsDTOs.forEach(itemDTO -> {
            cart.getCartItems().stream()
                    .filter(shoppingCartItem -> Objects.equals(shoppingCartItem.getSkuCode(), itemDTO.skuCode()))
                    .findFirst()
                    .ifPresentOrElse(existentItem -> {
                                log.trace("removeItems: exists itemDTO={}", itemDTO);
                                if (existentItem.getQuantity() < itemDTO.quantity())
                                    throw new InsufficientQuantityException(existentItem);

                                existentItem.setQuantity(existentItem.getQuantity() - itemDTO.quantity());
                                log.debug("new Quantity={}", existentItem.getQuantity());

                                if (existentItem.getQuantity() == 0) {
                                    emptySkuCodes.add(existentItem);
                                }
                            },
                            () -> {
                                log.trace("removeItems: not exist itemDTO={}", itemDTO);
                                final var item = cartMapper.toItem(itemDTO);
                                final var inventoryDetails = cartReadService.getInventoryDetails(item);
                                item.setName(inventoryDetails.name());
                                throw new NotFoundException(item);
                            });


        });

        cart.removeItems(emptySkuCodes);


        cart.setTotalAmount(totalAmount(cart.getCartItems()));
        cart.setComplete(cart.getCartItems().isEmpty());
        log.debug("die quantities: {}", cart.getCartItems());
        final var newCart = cartRepository.save(cart);
        log.debug("newCart: {}", newCart);
        log.debug("die quantities: {}", cart.getCartItems());

        return STR."der neue Wert des Warenkorb beträgt \{cart.getTotalAmount()}€";
    }


    private BigDecimal totalAmount(List<Item> items) {
        log.debug("totalAmount: items={}", items);

        final BigDecimal[] totalAmount = {new BigDecimal(0)};
        items.forEach(orderedItem -> {
            BigDecimal itemTotal = orderedItem.getPrice().multiply(BigDecimal.valueOf(orderedItem.getQuantity()));
            totalAmount[0] = totalAmount[0].add(itemTotal);
        });
        log.debug("totalAmount: totalAmount={}", totalAmount[0]);
        return totalAmount[0];
    }

    public String deleteById(UUID id) {
        final var cart = cartRepository.findById(id).orElseThrow(NotFoundException::new);
        cartRepository.delete(cart);
        return STR."der Warenkorb von \{cart.getCustomerUsername()} wurde gelöscht";
    }

    public String deleteByCustomerId(UUID id) {
        final var cart = cartRepository.findByCustomerId(id).orElseThrow(NotFoundException::new);
        cartRepository.delete(cart);
        return STR."der Warenkorb von \{cart.getCustomerUsername()} wurde gelöscht";
    }


}

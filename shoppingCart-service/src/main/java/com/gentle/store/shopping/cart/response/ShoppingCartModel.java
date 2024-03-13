package com.gentle.store.shopping.cart.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentle.store.shopping.cart.entity.ShoppingCart;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.util.UUID;

@JsonPropertyOrder({
        "totalAmount", "customerId", "customerUsername"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class ShoppingCartModel extends RepresentationModel<ShoppingCartModel> {
    private final BigDecimal totalAmount;
    private final UUID customerId;
    private final String customerUsername;

    public ShoppingCartModel(final ShoppingCart shoppingCart) {
        totalAmount = shoppingCart.getTotalAmount();
        customerId = shoppingCart.getCustomerId();
        customerUsername = shoppingCart.getCustomerUsername();
    }
}

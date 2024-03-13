package com.gentle.store.inventory.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentle.store.inventory.entity.Inventory;
import com.gentle.store.inventory.entity.InventoryStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.util.UUID;

@JsonPropertyOrder({
        "productId", "productName", "skuCode", "quantity", "unitPrice", "status"
})
@Relation(collectionRelation = "inventories", itemRelation = "inventory")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class InventoryModel extends RepresentationModel<InventoryModel> {

    @EqualsAndHashCode.Include
    private final String skuCode;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final UUID productId;
    private final String productName;
    private final InventoryStatusType status;

    public InventoryModel(final Inventory inventory) {
        skuCode     = inventory.getSkuCode();
        quantity    = inventory.getQuantity();
        unitPrice   = inventory.getUnitPrice();
        status      = inventory.getStatus();
        productId   = inventory.getProductId();
        productName = inventory.getProductName();
    }
}

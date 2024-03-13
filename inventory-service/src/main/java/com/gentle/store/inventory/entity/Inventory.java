package com.gentle.store.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.gentle.store.inventory.util.Constants.RESERVED_PRODUCTS_GRAPH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

@Getter
@Setter
@Entity
@Table(name = "inventory")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(name = RESERVED_PRODUCTS_GRAPH, attributeNodes = @NamedAttributeNode("reservedProductsList"))
@ToString
public class Inventory {

    @Id
    @GeneratedValue
    private UUID id;

    @Version
    private  int version;

    private String skuCode;

    private Integer quantity;

    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private InventoryStatusType status;

    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<ReservedProduct> reservedProductsList;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    private UUID productId;
    @Transient
    private String productName;

    public void set(Inventory inventory) {
        skuCode   = inventory.getSkuCode();
        quantity  = inventory.getQuantity();
        unitPrice = inventory.getUnitPrice();
        status    = inventory.getStatus();
        productId = inventory.getProductId();
    }

    public void addReservedProducts(List<ReservedProduct> items) {
        reservedProductsList.addAll(items);
    }

    public void removeReservation(List<ReservedProduct> reservedProducts) {
        reservedProductsList.removeAll(reservedProducts);
    }

}
package com.gentle.store.shopping.cart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.UUID;


/**
 * Entität, die einen Artikel im Warenkorb eines Online-Shops repräsentiert.
 */
@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    /**
     * Eindeutige ID des Artikels.
     */
    @Id
    @GeneratedValue
    private UUID id;
    /**
     * SKU-Code des Artikels.
     */
    @Column(name = "sku_code")
    private String skuCode;
    /**
     * Menge des Artikels im Warenkorb.
     */
    @Range(message = "Die Menge muss mindestens 1 sein", min = 1)
    @Column(nullable = false)

    private int quantity;
    /**
     * Preis des Artikels.
     */
    @Transient
    private BigDecimal price;
    /**
     * Name des Artikels.
     */
    @Transient
    private String name;

    public Item(Item existingItem) {
        this.id       = existingItem.id;
        this.quantity = existingItem.quantity;
        this.name     = existingItem.name;
        this.price    = existingItem.price;
        this.skuCode  = existingItem.skuCode;
    }
}

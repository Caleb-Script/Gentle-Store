package com.gentle.store.shopping.cart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.gentle.store.shopping.cart.util.Constants.ITEM_GRAPH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

/**
 * Entität, die einen Einkaufswagen im Online-Shop darstellt.
 * */
@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NamedEntityGraph(name = ITEM_GRAPH, attributeNodes = @NamedAttributeNode("cartItems"))
public class ShoppingCart {
    /**
     * Die eindeutige ID des Einkaufswagens.
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;
    /**
     * Die Versionsnummer des Einkaufswagens.
     */
    @Version
    private int version;
    /**
     * Die Gesamtsumme des Warenkorbs
     */
    @DecimalMin(value = "0.0", message = "Die Gesamtsumme muss größer als 0 sein")
    private BigDecimal totalAmount;
    /**
     * die Kunden-ID
     */
    @NotNull(message = "Die Kunden-ID darf nicht null sein")
    private UUID customerId;
    /**
     * Benutzernamen des Kunden
     */
    @Column(name = "customer_username")
    private String customerUsername;
    /**
     * Status des Warenkorbs,
     * gibt an, ob der Einkaufswagen abgeschlossen ist
     */
    @Column(name = "is_complete")
    private boolean isComplete;

    /**
     * Liste der Artikel im Einkaufswagen.
     */
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "shopping_cart_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<Item> cartItems;

    /**
     * Zeitpunkt der Erstellung des Einkaufswagens.
     */
   @CreationTimestamp
    private LocalDateTime created;
    /**
     * Zeitpunkt der letzten Aktualisierung des Einkaufswagens.
     */
    @UpdateTimestamp
    private LocalDateTime updated;

    /**
     * Fügt eine Liste von Artikeln dem Einkaufswagen hinzu.
     *
     * @param items Die Liste der hinzuzufügenden Artikel.
     */
    public void addItems(List<Item> items) {
        cartItems.addAll(items);
    }

    public void removeItems(List<Item> items) {
        cartItems.removeAll(items);
    }
}

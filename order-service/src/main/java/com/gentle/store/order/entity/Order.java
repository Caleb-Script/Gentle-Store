package com.gentle.store.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.gentle.store.order.util.Constants.ITEM_GRAPH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(name = ITEM_GRAPH, attributeNodes = @NamedAttributeNode("orderedItems"))
@ToString
public class Order {
    @Id
    @GeneratedValue
    private UUID id;

    @Version
    private int version;

    private String orderNumber;
    private boolean isComplete;

    private UUID customerId;
    private BigDecimal totalAmount;

    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<OrderedItem> orderedItems;

    public void addItems(List<OrderedItem> items) {
        orderedItems.addAll(items);
    }

}
package com.gentle.store.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ordered_item")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderedItem {
    @Id
    @GeneratedValue
    private UUID id;

    private String skuCode;

    @Transient
    private BigDecimal price;

    private Integer quantity;
}
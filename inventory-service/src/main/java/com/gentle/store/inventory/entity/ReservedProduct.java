package com.gentle.store.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reserved_products")
@ToString
public class ReservedProduct {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "sku_code")
    private String skuCode;

    private int quantity;

    @Column(name = "customer_id")
    private UUID customerId;
}

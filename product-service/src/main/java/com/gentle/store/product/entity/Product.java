package com.gentle.store.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Product {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    private int version;

    private String name;

    private String brand;

    private BigDecimal price;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductCategoryType category;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    public void set(Product product) {
        name        = product.getName();
        brand       = product.getBrand();
        price       = product.getPrice();
        description = product.getDescription();
        category    = product.getCategory();
    }
}
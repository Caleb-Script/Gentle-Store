package com.gentle.store.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "payment")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    private LocalDateTime paymentDate;

    @OneToMany(orphanRemoval = true)
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<Item> payedItems;

    private  UUID customerId;

    @Column(name = "order_number")
    private String orderNumber;
}

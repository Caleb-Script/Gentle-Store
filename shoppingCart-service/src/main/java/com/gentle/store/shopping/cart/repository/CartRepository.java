package com.gentle.store.shopping.cart.repository;

import com.gentle.store.shopping.cart.entity.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.shopping.cart.util.Constants.ITEM_GRAPH;

public interface CartRepository extends JpaRepository<ShoppingCart, UUID>{
    @Override
    @NonNull
    Optional<ShoppingCart> findById(@NonNull final UUID uuid);

    @NonNull
    Optional<ShoppingCart>findByCustomerId(@NonNull final UUID customerId);

    @NonNull
    @Override
    List<ShoppingCart> findAll();

    @Query("""
        SELECT DISTINCT C
        FROM     ShoppingCart C
        WHERE    C.id = :id
        """)
    @EntityGraph(ITEM_GRAPH)
    Optional<ShoppingCart> findByIdFetchAll(final UUID id);

    @Query("""
        SELECT DISTINCT C
        FROM     ShoppingCart C
        WHERE    C.customerId = :customerId
        """)
    @EntityGraph(ITEM_GRAPH)
    Optional<ShoppingCart> findByCustomerIdFetchAll(final UUID customerId);
}
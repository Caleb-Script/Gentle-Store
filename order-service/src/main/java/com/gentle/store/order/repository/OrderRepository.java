package com.gentle.store.order.repository;

import com.gentle.store.order.entity.Order;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.order.util.Constants.ITEM_GRAPH;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @NonNull
    Optional<Order> findByOrderNumber(@NonNull String orderNumber);

    @Query("""
        SELECT DISTINCT O
        FROM     Order O
        WHERE    O.id = :id
        """)
    @NonNull
    @EntityGraph(ITEM_GRAPH)
    Optional<Order> findByIdFetchAll(@NonNull UUID id);
}
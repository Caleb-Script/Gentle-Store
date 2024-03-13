package com.gentle.store.inventory.repository;

import com.gentle.store.inventory.entity.Inventory;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.inventory.util.Constants.RESERVED_PRODUCTS_GRAPH;

public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {

    List<Inventory> findBySkuCodeIn(List<String> skuCode);

    @NonNull
    Optional<Inventory> findBySkuCode(@NonNull String skuCode);

    @Override
    @NonNull
    Optional<Inventory> findById(@NonNull UUID uuid);

    @Override
    @NonNull
    List<Inventory> findAll();

    @Override
    @NonNull
    List<Inventory> findAll(@NonNull Specification<Inventory> spec);

    @Query("""
        SELECT DISTINCT I
        FROM     Inventory I
        WHERE    I.id = :id
        """)
    @NonNull
    @EntityGraph(RESERVED_PRODUCTS_GRAPH)
    Optional<Inventory> findByIdFetchAll(UUID id);

    @Query("""
        SELECT DISTINCT I
        FROM     Inventory I
        WHERE    I.skuCode = :skuCode
        """)
    @NonNull
    @EntityGraph(RESERVED_PRODUCTS_GRAPH)
    Optional<Inventory> findBySkuCodeFetchAll(String skuCode);
}
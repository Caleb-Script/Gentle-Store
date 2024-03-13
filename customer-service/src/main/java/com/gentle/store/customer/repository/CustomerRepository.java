package com.gentle.store.customer.repository;

import com.gentle.store.customer.entity.Customer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.store.customer.util.Constants.ADDRESS_GRAPH;
import static com.gentle.store.customer.util.Constants.ALL_GRAPH;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    @EntityGraph(ADDRESS_GRAPH)
    @NonNull
    @Override
    List<Customer> findAll();

    @EntityGraph(ADDRESS_GRAPH)
    @NonNull
    @Override
    List<Customer> findAll(@NonNull Specification<Customer> spec);

    @EntityGraph(ADDRESS_GRAPH)
    @NonNull
    @Override
    Optional<Customer> findById(@NonNull UUID id);

    @Query("""
        SELECT DISTINCT C
        FROM     Customer C
        WHERE    C.id = :id
        """)
    @EntityGraph(ALL_GRAPH)
    @NonNull
    Optional<Customer> findByIdFetchAll(UUID id);
    
    @Query("""
        SELECT C
        FROM   Customer C
        WHERE  lower(C.email) LIKE concat(lower(:email), '%')
        """)
    @EntityGraph(ADDRESS_GRAPH)
    Collection<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT   C
        FROM     Customer C
        WHERE    lower(C.surname) LIKE concat('%', lower(:surname), '%')
        ORDER BY C.id
        """)
    @EntityGraph(ADDRESS_GRAPH)
    Collection<Customer> findByLastName(CharSequence surname);

    @Query("""
        SELECT DISTINCT C.surname
        FROM     Customer C
        WHERE    lower(C.surname) LIKE concat(lower(:prefix), '%')
        ORDER BY C.surname
        """)
    Collection<String> findSurnamesByPrefix(String prefix);

    Optional<Customer> findByUsername(String username);
}

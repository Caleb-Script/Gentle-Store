package com.gentle.store.product.repository;

import com.gentle.store.product.entity.Product;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @Override
    @NonNull
    List<Product> findAll(@NonNull Specification<Product> spec);

    @org.springframework.lang.NonNull
    @Override
    List<Product> findAll();


    @NonNull
    Optional<Product> findById(@NonNull UUID id);


}
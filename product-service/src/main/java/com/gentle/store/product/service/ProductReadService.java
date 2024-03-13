package com.gentle.store.product.service;

import com.gentle.store.product.entity.Product;
import com.gentle.store.product.repository.ProductRepository;
import com.gentle.store.product.repository.SpecificationBuilder;
import com.gentle.store.product.service.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductReadService {
    private final ProductRepository productRepository;
    private final SpecificationBuilder specificationBuilder;

    public Product findById(UUID id) {
        log.debug("findById: id={}",id);

        final var product = productRepository.findById(id).orElseThrow(NotFoundException::new);
        log.debug("findById: product={}",product);
        return product;
    }

    public @NonNull Collection<Product> find(@NonNull final Map<String, List<String>> searchCriteria) {
        log.debug("find: searchCriteria={}", searchCriteria);

        if (searchCriteria.isEmpty()) {
            return productRepository.findAll();
        }

        final var specification = specificationBuilder
                .build(searchCriteria)
                .orElseThrow(() -> new NotFoundException(searchCriteria));
        final var products = productRepository.findAll(specification);

        if (products.isEmpty())
            throw new NotFoundException(searchCriteria);

        log.debug("find: products={}", products);
        return products;
    }
}

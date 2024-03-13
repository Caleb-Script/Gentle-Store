package com.gentle.store.product.dto;

import com.gentle.store.product.entity.Product;
import com.gentle.store.product.entity.ProductCategoryType;

import java.math.BigDecimal;

/**
 * DTO for {@link Product}
 */
public record ProductDTO(
        String name,
        String brand,
        BigDecimal price,
        String description,
        ProductCategoryType category,
        String skuCode
) {
}
package com.gentle.store.product.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentle.store.product.entity.Product;
import com.gentle.store.product.entity.ProductCategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@JsonPropertyOrder({
        "name", "brand", "description", "category", "price"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class ProductModel extends RepresentationModel<ProductModel> {

    private final String name;

    private final String brand;

    private final BigDecimal price;

    private final String description;

    private final ProductCategoryType category;

    public ProductModel(final Product product) {
        name        = product.getName();
        brand       = product.getBrand();
        price       = product.getPrice();
        description = product.getDescription();
        category    = product.getCategory();
    }
}

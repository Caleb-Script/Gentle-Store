package com.gentle.store.product.repository;

import com.gentle.store.product.entity.Product;
import com.gentle.store.product.entity.ProductCategoryType;
import com.gentle.store.product.entity.Product_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificationBuilder {
    /**
     * Specification für eine Query mit Spring Data bauen.
     *
     * @param queryParams als MultiValueMap
     * @return Specification für eine Query mit Spring Data
     */
    public Optional<Specification<Product>> build(final Map<String, ? extends List<String>> queryParams) {
        log.debug("build: queryParams={}", queryParams);

        if (queryParams.isEmpty()) {
            // keine Suchkriterien
            return Optional.empty();
        }

        final var specs = queryParams
            .entrySet()
            .stream()
            .map(this::toSpecification)
            .toList();

        if (specs.isEmpty() || specs.contains(null)) {
            return Optional.empty();
        }

        return Optional.of(Specification.allOf(specs));
    }

    private Specification<Product> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
        log.trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();

        if (values == null || values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "brand" -> brand(value);
            case "category" -> category(value);
            case "name" -> name(value);
            case "maxPrice" -> maxPrice(value);
            case "minPrice" -> minPrice(value);
            default -> null;
        };
    }


    private Specification<Product> name(final String name) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Product_.name)),
            builder.lower(builder.literal(STR."%\{name}%"))
        );
    }

    private Specification<Product> brand(final String brand) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get(Product_.brand)),
                builder.lower(builder.literal(STR."%\{brand}%"))
        );
    }

    private Specification<Product> category(final String category) {
        return (root, query, builder) -> builder.equal(
                root.get(Product_.category),
                ProductCategoryType.of(category)
        );
    }

    private Specification<Product> maxPrice(String end) {
        final var endPrice = new BigDecimal(end);
        return (root,query, builder) -> builder.lessThanOrEqualTo(root.get(Product_.price), endPrice);
    }

    private Specification<Product> minPrice(String start) {
        final var startPrice = new BigDecimal(start);
        return (root,query, builder) -> builder.greaterThanOrEqualTo(root.get(Product_.price), startPrice);
    }
}

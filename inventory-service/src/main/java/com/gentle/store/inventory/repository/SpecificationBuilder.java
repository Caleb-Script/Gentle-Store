package com.gentle.store.inventory.repository;

import com.gentle.store.inventory.entity.*;
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
    public Optional<Specification<Inventory>> build(final Map<String, ? extends List<String>> queryParams) {
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

    private Specification<Inventory> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
        log.trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();

        if (values == null || values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "skuCode" -> skuCode(value);
            case "maxQuantity" -> maxQuantity(value);
            case "minQuantity" -> minQuantity(value);
            case "status" -> status(value);
            case "name" -> name(value);
            case "maxPrice" -> maxPrice(value);
            case "minPrice" -> minPrice(value);
            default -> null;
        };
    }


    private Specification<Inventory> name(final String name) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Inventory_.productId.getName())),
            builder.lower(builder.literal(STR."%\{name}%"))
        );
    }

    private Specification<Inventory> skuCode(final String skuCode) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get(Inventory_.skuCode)),
                builder.lower(builder.literal(STR."%\{skuCode}%"))
        );
    }

    private Specification<Inventory> minQuantity(final String quantityString) {
        final var minQuantity = Integer.parseInt(quantityString);
        return (root,query, builder) -> builder.greaterThanOrEqualTo(root.get(Inventory_.quantity), minQuantity);
    }

    private Specification<Inventory> maxQuantity(final String quantityString) {
        final var maxQuantity = Integer.parseInt(quantityString);
        return (root,query, builder) -> builder.lessThanOrEqualTo(root.get(Inventory_.quantity), maxQuantity);
    }

    private Specification<Inventory> status(final String status) {
        return (root, query, builder) -> builder.equal(
                root.get(Inventory_.status),
                InventoryStatusType.of(status)
        );
    }

    private Specification<Inventory> maxPrice(String end) {
        final var endPrice = new BigDecimal(end);
        return (root,query, builder) -> builder.lessThanOrEqualTo(root.get(Inventory_.unitPrice), endPrice);
    }

    private Specification<Inventory> minPrice(String start) {
        final var startPrice = new BigDecimal(start);
        return (root,query, builder) -> builder.greaterThanOrEqualTo(root.get(Inventory_.unitPrice), startPrice);
    }
}

package com.gentle.store.customer.repository;

import com.gentle.store.customer.entity.*;
import com.gentle.store.customer.entity.enums.*;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class SpecificationBuilder {
    /**
     * Specification für eine Query mit Spring Data bauen.
     *
     * @param queryParams als MultiValueMap
     * @return Specification für eine Query mit Spring Data
     */
    public Optional<Specification<Customer>> build(final Map<String, ? extends List<String>> queryParams) {
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

    private Specification<Customer> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
        log.trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();
        if ("interest".contentEquals(key)) {
            return toSpecificationInterests(values);
        }
        if ("contact".contentEquals(key)) {
            return toSpecificationContactOptions(values);
        }

        if (values == null || values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "surname" -> surname(value);
            case "username" -> username(value);
            case "email" ->  email(value);
            case "category" -> category(value);
            case "newsletter" -> newsletter(value);
            case "gender" -> gender(value);
            case "maritalStatus" -> maritalStatus(value);
            case "status" -> status(value);
            case "zipCode" -> zipCode(value);
            case "city" -> city(value);
            case "state" -> state(value);
            case "dialCode" -> dialCode(value);
            case "activityType" -> activityType(value);
            default -> null;
        };
    }

    private Specification<Customer> toSpecificationContactOptions(final Collection<String> options) {
        log.trace("build: contactOptions={}", options);
        if(options == null || options.isEmpty()) {
            return null;
        }

        final var specsImmutable = options.stream()
                .map(this::contactOptions)
                .toList();
        if (specsImmutable.isEmpty() || specsImmutable.contains(null)) {
            return null;
        }
        final List<Specification<Customer>> specs = new ArrayList<>(specsImmutable);
        final var first = specs.removeFirst();
        return specs.stream().reduce(first, Specification::and);
    }

    private Specification<Customer> toSpecificationInterests(final Collection<String> interests) {
        log.trace("build: interests={}", interests);
        if (interests == null || interests.isEmpty()) {
            return null;
        }

        final var specsImmutable = interests.stream()
            .map(this::interest)
            .toList();
        if (specsImmutable.isEmpty() || specsImmutable.contains(null)) {
            return null;
        }

        final List<Specification<Customer>> specs = new ArrayList<>(specsImmutable);
        final var first = specs.removeFirst();
        return specs.stream().reduce(first, Specification::and);
    }

    private Specification<Customer> username(String value) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get(Customer_.username)),
                builder.lower(builder.literal(STR."%\{value}%"))
        );
    }

    private Specification<Customer> surname(final String teil) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Customer_.surname)),
            builder.lower(builder.literal(STR."%\{teil}%"))
        );
    }

    private Specification<Customer> email(final String teil) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Customer_.email)),
            builder.lower(builder.literal(STR."%\{teil}%"))
        );
    }

    private Specification<Customer> category(final String kategorie) {
        final int kategorieInt;
        try {
            kategorieInt = Integer.parseInt(kategorie);
        } catch (final NumberFormatException e) {

            return null;
        }
        return (root, query, builder) -> builder.equal(root.get(Customer_.customerCategory), kategorieInt);
    }

    private Specification<Customer> newsletter(final String hasNewsletter) {
        return (root, query, builder) -> builder.equal(
            root.get(Customer_.hasNewsletter),
            Boolean.parseBoolean(hasNewsletter)
        );
    }

    private Specification<Customer> gender(final String geschlecht) {
        return (root, query, builder) -> builder.equal(
            root.get(Customer_.gender),
           GenderType.of(geschlecht)
        );
    }

    private Specification<Customer> maritalStatus(final String familienstand) {
        return (root, query, builder) -> builder.equal(
            root.get(Customer_.maritalStatus),
            MaritalStatusType.of(familienstand)
        );
    }

    private Specification<Customer> status(final String status) {
        return (root, query, builder) -> builder.equal(
                root.get(Customer_.status),
                CustomerStatusType.of(status)
        );
    }

    private Specification<Customer> interest(final String interests) {
        final var interestType = InterestType.of(interests);
        if (interestType == null) {
            return null;
        }
        return (root, query, builder) -> builder.like(
            root.get(Customer_.interestsString),
            builder.literal(STR."%\{interestType.name()}%")
        );
    }

    private Specification<Customer> contactOptions(final String option) {
        final var contactOptionsType = ContactOptionsType.of(option);
        if (contactOptionsType == null) {
            return null;
        }
        return (root, query, builder) -> builder.like(
                root.get(Customer_.contactOptionsString),
                builder.literal(STR."%\{contactOptionsType.name()}%")
        );
    }

    private Specification<Customer> zipCode(final String prefix) {
        return (root, query, builder) -> builder.like(root.get(Customer_.address).get(Address_.zipCode), STR."%\{prefix}%");
    }

    private Specification<Customer> city(final String prefix) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Customer_.address).get(Address_.city)),
            builder.lower(builder.literal(STR."%\{prefix}%"))
        );
    }

    private Specification<Customer> state(final String familienstand) {
        return (root, query, builder) -> builder.equal(
                root.get(Customer_.address).get(Address_.state),
                StateType.of(familienstand)
        );
    }

    private Specification<Customer> dialCode(final String prefix) {
        return (root, query, builder) -> {
            Join<Customer, PhoneNumber> phoneNumberJoin = root.join(Customer_.phoneNumberList);
            return builder.like(
                    builder.lower(phoneNumberJoin.get(PhoneNumber_.dialingCode)),
                    builder.lower(builder.literal(STR."\{prefix}"))
            );
        };
    }

    private Specification<Customer> activityType(final String prefix) {
        return (root, query, builder) -> {
            Join<Customer, CustomerActivity> activitiesJoin = root.join(Customer_.activities);
            return builder.equal(
                    activitiesJoin.get(CustomerActivity_.activityType),
                            ActivityType.of(prefix));
        };
    }
}

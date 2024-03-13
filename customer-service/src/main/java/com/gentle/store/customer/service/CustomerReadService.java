package com.gentle.store.customer.service;

import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.repository.CustomerRepository;
import com.gentle.store.customer.repository.SpecificationBuilder;
import com.gentle.store.customer.security.Role;
import com.gentle.store.customer.service.exception.AccessForbiddenException;
import com.gentle.store.customer.service.exception.NotFoundException;
import com.gentle.store.customer.transfer.Account;
import io.micrometer.observation.annotation.Observed;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.gentle.store.customer.security.Role.ADMIN;
import static com.gentle.store.customer.util.Constants.ADMIN_BASIC_AUTH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerReadService {
    private final CustomerRepository customerRepository;
    private final SpecificationBuilder specificationBuilder;
    
    @Observed(name = "find-by-id")
    public @NonNull Customer findById(final UUID id, final UserDetails user, final boolean fetchAll) {
        log.debug("findById: id={}, user={}", id, user);

        final var customer = fetchAll
                ? customerRepository.findByIdFetchAll(id).orElseThrow(NotFoundException::new)
                : customerRepository.findById(id).orElseThrow(NotFoundException::new);

        if (customer.getUsername().contentEquals(user.getUsername())) {
            //eigene Kunden Daten
            return customer;
        }

        final var roles = user
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(Role.ROLE_PREFIX.length()))
            .map(Role::valueOf)
            .toList();
        if (!roles.contains(ADMIN)) {
            // nicht admin, aber keine eigenen (oder keine) Kundendaten
            throw new AccessForbiddenException(roles);
        }

        log.debug("findById: customer={}", customer);

        if (fetchAll) {
            log.debug("findById: activities={}", customer.getActivities());
            log.debug("findById: phoneNumberList={}", customer.getPhoneNumberList());
        }
        return customer;
    }

    /**
     * Kunden anhand von Suchkriterien als Collection suchen.
     *
     * @param searchCriteria Die Suchkriterien
     * @return Die gefundenen Kunden oder eine leere Liste
     * @throws NotFoundException Falls keine Kunden gefunden wurden
     */
    public @NonNull Collection<Customer> find(@NonNull final Map<String, List<String>> searchCriteria) {
        log.debug("find: searchCriteria={}", searchCriteria);

        if (searchCriteria.isEmpty()) {
            return customerRepository.findAll();
        }

        if (searchCriteria.size() == 1) {
            final var surnames = searchCriteria.get("surname");
            if (surnames != null && surnames.size() == 1) {
                log.debug("searchCriteria: (surnames): {}", surnames.getFirst());
                final var customers = customerRepository.findByLastName(surnames.getFirst());
                if(customers.isEmpty()) {
                    throw new NotFoundException(searchCriteria);
                }
                final var customerList = List.of(customers).getFirst();
                log.debug("find (surnames): {}", customerList);
                return customerList;
            }

            final var emails = searchCriteria.get("email");
            if (emails != null && emails.size() == 1) {
                log.debug("searchCriteria: (email): {}", emails.getFirst());
                final var customers = customerRepository.findByEmail(emails.getFirst());
                if(customers.isEmpty()) {
                    throw new NotFoundException(searchCriteria);
                }
                final var customerList = List.of(customers).getFirst();
                log.debug("find (email): {}", customerList);
                return customerList;
            }
        }

        final var specification = specificationBuilder
            .build(searchCriteria)
            .orElseThrow(() -> new NotFoundException(searchCriteria));
        final var customers = customerRepository.findAll(specification);

        if (customers.isEmpty())
            throw new NotFoundException(searchCriteria);

        log.debug("find: customers={}", customers);
        return customers;
    }

    /**
     * Abfrage, welche Nachnamen es zu einem Präfix gibt.
     *
     * @param prefix Nachname-Präfix.
     * @return Die passenden Nachnamen.
     * @throws NotFoundException Falls keine Nachnamen gefunden wurden.
     */
    public @NonNull Collection<String> findSurnamesByPrefix(final String prefix) {
        log.debug("findSurnamesByPrefix: {}", prefix);

        final var surnames = customerRepository.findSurnamesByPrefix(prefix);
        if(surnames.isEmpty()) {
            throw new NotFoundException();
        }
        final var surnameList = List.of(surnames).getFirst();

        log.debug("findNachnamenByPrefix: surnames={}", surnameList);
        return surnameList;
    }
}

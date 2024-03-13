package com.gentle.store.customer.service.patch;

import com.gentle.store.customer.dto.PhoneNumberDTO;
import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.entity.enums.ContactOptionsType;
import com.gentle.store.customer.entity.enums.GenderType;
import com.gentle.store.customer.entity.enums.InterestType;
import com.gentle.store.customer.entity.enums.MaritalStatusType;
import com.gentle.store.customer.mapper.CustomerMapper;
import com.google.common.base.Splitter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import static com.gentle.store.customer.service.patch.PatchOperationType.*;

@Component
@Slf4j
@RequiredArgsConstructor
public final class CustomerPatcher {
    private final CustomerMapper customerMapper;

    /**
     * PATCH-Operationen werden auf ein Customer-Objekt angewandt.
     *
     * @param customer      Das zu modifizierende Customer-Objekt.
     * @param operations Die anzuwendenden Operationen.
     * @param request    Das Request-Objekt, um ggf. die URL für ProblemDetail zu ermitteln
     * @throws InvalidPatchOperationException Falls die Patch-Operation nicht korrekt ist.
     */
    public void patch(
            final Customer customer,
            final Collection<PatchOperation> operations,
            final HttpServletRequest request
    ) {
        final var replaceOps = operations.stream()
                .filter(op -> op.operationType() == REPLACE)
                .toList();
        log.debug("patch: replaceOps={}", replaceOps);
        final var uri = URI.create(request.getRequestURL().toString());
        replaceOps(customer, replaceOps, uri);

        final var addOps = operations.stream()
                .filter(op -> op.operationType() == ADD)
                .toList();
        log.debug("patch: addOps={}", addOps);
        addOps(customer, addOps, uri);

        final var removeOps = operations.stream()
                .filter(op -> op.operationType() == REMOVE)
                .toList();
        log.debug("patch: removeOps={}", removeOps);
        removeOps(customer, removeOps, uri);
    }

    private void replaceOps(final Customer customer, @NonNull final Iterable<@NonNull PatchOperation> ops, final URI uri) {
        ops.forEach(op -> {
            switch (op.path()) {
                case "surname" -> customer.setSurname(op.value());
                case "email" -> customer.setEmail(op.value());
                case "gender" -> customer.setGender(GenderType.of(op.value()));
                case "maritalStatus" -> customer.setMaritalStatus(MaritalStatusType.of(op.value()));
                default -> throw new InvalidPatchOperationException(uri);
            }
        });
        log.trace("replaceOps: customer={}", customer);
    }

    private void addOps(final Customer customer, final Collection<PatchOperation> ops, final URI uri) {
        ops.forEach(op -> {
            switch (op.path()) {
                case "interest" -> addInterest(customer, op, uri);
                case "contactOption" -> addContactOption(customer, op, uri);
                case "phoneNumber" -> addPhoneNumber(customer, op, uri);
                default -> throw new InvalidPatchOperationException(uri);
            }
        });
        log.trace("addOps: customer={}", customer);
    }

    private void removeOps(final Customer customer, @NonNull final Collection<@NonNull PatchOperation> ops, final URI uri) {
        ops.forEach(op -> {
            switch (op.path()) {
                case "interest" -> removeInterest(customer, op, uri);
                case "contactOption" -> removeContactOption(customer, op, uri);
                case "phoneNumber" -> removePhoneNumber(customer, op, uri);
                default -> throw new InvalidPatchOperationException(uri);
            }
        });
        log.trace("removeOps: customer={}", customer);
    }

    private void addInterest(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("adding interes={}", op.value());

        final var interest = InterestType.of(op.value());
        if (interest == null) 
            throw new InvalidPatchOperationException(uri);
        

        final var interests = customer.getInterests() == null
                ? new ArrayList<InterestType>(InterestType.values().length)
                : new ArrayList<>(customer.getInterests());

        if (interests.contains(interest))
            throw new InvalidPatchOperationException(uri);
        
        interests.add(interest);

        log.trace("addInterest: interests={}", op, interests);
        customer.setInterests(interests);
    }
        
    private void addContactOption(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("adding contactOption={}",op.value());

        final var contactOption = ContactOptionsType.of(op.value());
        if (contactOption == null)
            throw new InvalidPatchOperationException(uri);
        
        final var contactOptions = customer.getContactOptions() == null
                ? new ArrayList<ContactOptionsType>(ContactOptionsType.values().length)
                : new ArrayList<>(customer.getContactOptions());
        if (contactOptions.contains(contactOption)) 
            throw new InvalidPatchOperationException(uri);
        
        contactOptions.add(contactOption);

        log.trace("addContactOption: contactOptions={}", op, contactOptions);
        customer.setContactOptions(contactOptions);
    }

    private void addPhoneNumber(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("adding phoneNumber={}",op.value());

        final var phoneNumber = Splitter.on("/").splitToList(op.value());

        if (phoneNumber.size() != 2) {
            throw new InvalidPatchOperationException(uri);
        }

        final var dialingCode = phoneNumber.getFirst();
        final var number = phoneNumber.getLast();
        final var newPhoneNumber = new PhoneNumberDTO(dialingCode, number, false);

        var phoneNumberList = customer.getPhoneNumberList();
        customer.addPhoneNumber(customerMapper.toPhoneNumber(newPhoneNumber));

        log.trace("addPhoneNumber: phoneNumberList={}", phoneNumberList);
    }

    private void removeContactOption(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("removing contactOption={}",op.value());

        final var contactOption = ContactOptionsType.of(op.value());
        if (contactOption == null) {
            throw new InvalidPatchOperationException(uri);
        }
        final var contactOptions = customer.getContactOptions()
                .stream()
                .filter(contactOptionTmp -> contactOptionTmp != contactOption)
                .toList();
        customer.setContactOptions(contactOptions);
        log.debug("removed contactOption={}", contactOption);
    }

    private void removePhoneNumber(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("removing phonNumber={}",op.value());

        final var phoneNumberString = Splitter.on("/").splitToList(op.value());

        if (phoneNumberString == null || phoneNumberString.size() != 2) {
            throw new InvalidPatchOperationException(uri);
        }

        final var dialingCode = phoneNumberString.getFirst();
        final var number = phoneNumberString.getLast();
//        final var phoneNumber= customer.getPhoneNumberList()
//                .stream()
//                .filter(phoneNumbertmp -> phoneNumbertmp.getDialingCode().equals(dialingCode) && phoneNumbertmp.getNumber().equals(number))
//                .toList()
//                        .getFirst();
        customer.removePhoneNumber(dialingCode,number);
        log.debug("removed phoneNumber");
    }

    private void removeInterest(final Customer kunde, final PatchOperation op, final URI uri) {
        log.debug("removing interestType={}",op.value());

        final var interestType = InterestType.of(op.value());
        if (interestType == null) {
            throw new InvalidPatchOperationException(uri);
        }
        final var interest = kunde.getInterests()
                .stream()
                .filter(interestTmp -> interestTmp != interestType)
                .toList();
        kunde.setInterests(interest);
        log.debug("removed interestType={}",interestType);
    }
}

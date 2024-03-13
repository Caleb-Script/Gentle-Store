package com.gentle.store.customer.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentle.store.customer.entity.Address;
import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.entity.enums.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
        "lastName", "firstName", "email", "telephone", "employment",
        "contactOptionsType", "customerCategory", "hasNewsletter",
        "birthDate", "homepage", "gender", "status", "maritalStatus",
        "interests", "address", "activities"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class CustomerModel extends RepresentationModel<CustomerModel> {
 private final String lastName;
    private final String firstName;
    @EqualsAndHashCode.Include
    private final String email;
    private final List<ContactOptionsType> contactOptionsType;
    private final Integer customerCategory;
    private final Boolean hasNewsletter;
    private final LocalDate birthDate;
    private final GenderType gender;
    private final CustomerStatusType status;
    private final MaritalStatusType maritalStatus;
    private final List<InterestType> interests;
    private final Address address;

    public CustomerModel(final Customer customer) {
        lastName = customer.getSurname();
        firstName = customer.getForename();
        email = customer.getEmail();
        contactOptionsType = customer.getContactOptions();
        customerCategory = customer.getCustomerCategory();
        hasNewsletter = customer.getHasNewsletter();
        birthDate = customer.getBirthDate();
        gender = customer.getGender();
        status = customer.getStatus();
        maritalStatus = customer.getMaritalStatus();
        interests = customer.getInterests();
        address = customer.getAddress();
    }
}

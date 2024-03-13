package com.gentle.store.customer.dto;

import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.entity.enums.ContactOptionsType;
import com.gentle.store.customer.entity.enums.GenderType;
import com.gentle.store.customer.entity.enums.InterestType;
import com.gentle.store.customer.entity.enums.MaritalStatusType;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Customer}
 */
public record CustomerDTO(
        String surname,
        String forename,
        String email,
        Integer customerCategory,
        Boolean hasNewsletter,
        LocalDate birthDate,
        URL homepage,
        GenderType gender,
        MaritalStatusType maritalStatus,
        List<InterestType> interests,
        List<ContactOptionsType> contactOptions,
        AddressDTO address,
        List<CustomerActivityDTO> activities,
        List<PhoneNumberDTO> phoneNumberList) {
    public CustomerDTO {
        activities = new ArrayList<>();
    }
}
package com.gentle.store.customer.dto;

import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.entity.enums.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Customer}
 */
public record CustomerUpdateDTO(
        String surname,
        String forename,
        String email,
        Integer customerCategory,
        Boolean hasNewsletter,
        LocalDate birthDate,
        URL homepage,
        GenderType gender,
        CustomerStatusType status,
        MaritalStatusType maritalStatus,
        List<ContactOptionsType> contactOptions,
        List<InterestType> interests,
        List<CustomerActivityDTO> activities,
        List<PhoneNumberDTO> phoneNumberList
) {
    public CustomerUpdateDTO {
        activities = new ArrayList<>();
        phoneNumberList = new ArrayList<>();
        interests = new ArrayList<>();
    }
}
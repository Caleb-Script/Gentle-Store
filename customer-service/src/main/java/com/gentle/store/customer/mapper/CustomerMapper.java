package com.gentle.store.customer.mapper;

import com.gentle.store.customer.dto.CustomerDTO;
import com.gentle.store.customer.dto.CustomerUpdateDTO;
import com.gentle.store.customer.dto.PhoneNumberDTO;
import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.entity.PhoneNumber;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {
    PhoneNumber toPhoneNumber(PhoneNumberDTO phoneNumberDTO);
    PhoneNumberDTO toPhoneNumberDTO(PhoneNumber phoneNumber);
    Customer toCustomer(CustomerDTO customerDTO);
    Customer toCustomer(CustomerUpdateDTO customerUpdateDTO);
    CustomerUpdateDTO toCustomerUpdateDTO(Customer customer);
}
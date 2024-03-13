package com.gentle.store.payment.mapper;

import com.gentle.store.payment.dto.PaymentDTO;
import com.gentle.store.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    Payment toPayment(PaymentDTO paymentDTO);
}
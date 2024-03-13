package com.gentle.store.payment.service;

import com.gentle.store.payment.dto.PaymentDTO;
import com.gentle.store.payment.mapper.PaymentMapper;
import com.gentle.store.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentWriteService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public String makePayment(final PaymentDTO paymentDTO, final UUID customerId) {
        log.info("makePayment (Service): paymentDTO={} customerId={}",paymentDTO,customerId);

        final var payment = paymentMapper.toPayment(paymentDTO);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCustomerId(customerId);
        log.debug("makePayment: payment={}",payment);

        final var newPayment = paymentRepository.save(payment);
        log.debug("makePayment: newPayment={}",newPayment);
        return "bezahlung erfolgt";
    }

}

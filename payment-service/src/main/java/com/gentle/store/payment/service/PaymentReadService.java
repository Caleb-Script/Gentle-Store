package com.gentle.store.payment.service;

import com.gentle.store.payment.entity.Payment;
import com.gentle.store.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentReadService {
    private final PaymentRepository paymentRepository;

    public Payment findById(final UUID id) {
        log.info("findById: id={}",id);

        return paymentRepository.findById(id).orElseThrow(NotFoundException::new);
    }
}

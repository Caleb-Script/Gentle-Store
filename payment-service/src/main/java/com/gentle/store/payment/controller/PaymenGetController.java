package com.gentle.store.payment.controller;

import com.gentle.store.payment.entity.Payment;
import com.gentle.store.payment.service.PaymentReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.gentle.store.payment.util.Constants.PAYMENT_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENT_PATH)
@Slf4j
public class PaymenGetController {

private final PaymentReadService paymentReadService;

@GetMapping("{customerId}")
Payment getById(@PathVariable final UUID customerId) {
    log.info("getById (Controller): customerId={}",customerId);
    return paymentReadService.findById(customerId);
}

}

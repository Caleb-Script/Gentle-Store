package com.gentle.store.payment.controller;

import com.gentle.store.payment.dto.PaymentDTO;
import com.gentle.store.payment.service.PaymentWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.gentle.store.payment.util.Constants.PAYMENT_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENT_PATH)
@Slf4j
public class PaymentWriteController {

private final PaymentWriteService paymentWriteService;

@PostMapping("{customerId}")
String makePayment(@PathVariable final UUID customerId, @RequestBody PaymentDTO paymentDTO) {
    log.info("makePayment (Controller): customerId={}, paymentDTO={}", customerId,paymentDTO);
    return paymentWriteService.makePayment(paymentDTO, customerId);
}

}

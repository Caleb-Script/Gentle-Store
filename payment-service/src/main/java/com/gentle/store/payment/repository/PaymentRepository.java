package com.gentle.store.payment.repository;

import com.gentle.store.payment.entity.Payment;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Override
    @NonNull
    Optional<Payment> findById(@NonNull UUID uuid);

    @Override
    @NonNull
    List<Payment> findAll();
}
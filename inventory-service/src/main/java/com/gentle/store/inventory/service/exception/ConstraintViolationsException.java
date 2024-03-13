package com.gentle.store.inventory.service.exception;

import com.gentle.store.inventory.entity.Inventory;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
    /**
     * Die verletzten Constraints.
     */
    private final transient Collection<ConstraintViolation<Inventory>> violations;

    public ConstraintViolationsException(
            final Collection<ConstraintViolation<Inventory>> violations
    ) {
        super("Constraints sind verletzt");
        this.violations = violations;
    }
}

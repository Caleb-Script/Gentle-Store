package com.gentle.store.product.service.exception;

import com.gentle.store.product.entity.Product;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
    /**
     * Die verletzten Constraints.
     */
    private final transient Collection<ConstraintViolation<Product>> violations;

    public ConstraintViolationsException(
            final Collection<ConstraintViolation<Product>> violations
    ) {
        super("Constraints sind verletzt");
        this.violations = violations;
    }
}

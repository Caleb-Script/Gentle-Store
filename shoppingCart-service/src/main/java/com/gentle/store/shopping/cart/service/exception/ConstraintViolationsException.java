package com.gentle.store.shopping.cart.service.exception;

import com.gentle.store.shopping.cart.entity.ShoppingCart;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
    /**
     * Die verletzten Constraints.
     */
    private final transient Collection<ConstraintViolation<ShoppingCart>> violations;

    public ConstraintViolationsException(
            final Collection<ConstraintViolation<ShoppingCart>> violations
    ) {
        super("Constraints sind verletzt");
        this.violations = violations;
    }
}

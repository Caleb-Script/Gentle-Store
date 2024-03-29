package com.gentle.store.customer.service.patch;

import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static com.gentle.store.customer.service.ProblemType.UNPROCESSABLE;
import static com.gentle.store.customer.util.Constants.PROBLEM_PATH;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class InvalidPatchOperationException extends ErrorResponseException {
    InvalidPatchOperationException(final URI uri) {
        super(UNPROCESSABLE_ENTITY, asProblemDetail(uri), null);
    }

    private static ProblemDetail asProblemDetail(final URI uri) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(
            UNPROCESSABLE_ENTITY,
            "Mindestens eine ungueltige Patch-Operation"
        );
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{UNPROCESSABLE.getValue()}"));
        problemDetail.setInstance(uri);
        return problemDetail;
    }
}

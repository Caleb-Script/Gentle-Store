package com.gentle.store.shopping.cart.service.exception;

import com.gentle.store.shopping.cart.service.ProblemType;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static com.gentle.store.shopping.cart.util.Constants.PROBLEM_PATH;

public class VersionInvalidException extends ErrorResponseException {
    public VersionInvalidException(final HttpStatusCode status, final String message, final URI uri) {
        this(status, message, uri, null);
    }

    public VersionInvalidException(
            final HttpStatusCode status,
            final String message,
            final URI uri,
            final Throwable cause
    ) {
        super(status, asProblemDetail(status, message, uri), cause);
    }

    private static ProblemDetail asProblemDetail(final HttpStatusCode status, final String detail, final URI uri) {
        final var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
        problemDetail.setInstance(uri);
        return problemDetail;
    }
}

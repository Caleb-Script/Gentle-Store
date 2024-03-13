package com.gentle.store.customer.service;

import com.gentle.store.customer.service.exception.AccessForbiddenException;
import com.gentle.store.customer.service.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

import static com.gentle.store.customer.util.Constants.PROBLEM_PATH;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
class RestCommonExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    ProblemDetail onNotFoundException(
            final NotFoundException ex,
            final HttpServletRequest request
    ) {
        log.error("onNotFound: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}/\{ProblemType.NOT_FOUND.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    @ResponseStatus(FORBIDDEN)
    ProblemDetail onAccessForbiddenException(final AccessForbiddenException ex, final HttpServletRequest request) {
        log.error("onEmailExists: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}/\{ProblemType.FORBIDDEN.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }
}

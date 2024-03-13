package com.gentle.store.customer.service;

import com.gentle.store.customer.service.exception.AccessForbiddenException;
import com.gentle.store.customer.service.exception.NotFoundException;
import graphql.GraphQLError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static org.springframework.graphql.execution.ErrorType.*;

@ControllerAdvice
@Slf4j
class GraphQlCommonExceptionHandler {
    @GraphQlExceptionHandler
    GraphQLError onNotFoundException(final NotFoundException ex) {
        log.error("onNotFound: {}", ex.getMessage());
        return GraphQLError.newError()
                .errorType(NOT_FOUND)
                .message(STR."Invalid Argument \{ex.getMessage()}.")
                .build();
    }

    @GraphQlExceptionHandler
    GraphQLError onAccessForbiddenException(final AccessForbiddenException ex) {
        log.error("onAccessForbidden: {}", ex.getMessage());
        return GraphQLError.newError()
                .errorType(FORBIDDEN)
                .message(STR."Invalid Argument \{ex.getMessage()}.")
                .build();
    }

    /**
     * Exception handler for IllegalArgumentException.
     *
     * @param ex The IllegalArgumentException.
     * @return A GraphQL error indicating an invalid argument.
     */
    @GraphQlExceptionHandler
    GraphQLError onIllegalArgumentException(final IllegalArgumentException ex) {
        log.error("onIllegalArgument: {}", ex.getMessage());
        return GraphQLError.newError()
                .errorType(BAD_REQUEST)
                .message(STR."Invalid Argument \{ex.getMessage()}.")
                .build();
    }

}


package io.cryptorush.userservice.graphql.validation;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
import io.cryptorush.userservice.domain.validation.BusinessFieldValidationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class GraphQlExceptionHandler implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        if (exception instanceof UserNotFoundException) {
            return Mono.fromCallable(() -> Collections.singletonList(
                    GraphqlErrorBuilder.newError(environment)
                            .errorType(ErrorType.NOT_FOUND)
                            .message(exception.getMessage())
                            .build()));
        }
        if (exception instanceof BusinessFieldValidationException) {
            return Mono.fromCallable(() -> Collections.singletonList(
                    GraphqlErrorBuilder.newError(environment)
                            .errorType(ErrorType.FORBIDDEN)
                            .message(exception.getMessage())
                            .build()));
        }
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            return Mono.fromCallable(() -> {
                Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
                List<GraphQLError> errors = new ArrayList<>(violations.size());
                for (ConstraintViolation<?> violation : violations) {
                    GraphQLError graphQLError = GraphqlErrorBuilder.newError(environment)
                            .errorType(ErrorType.BAD_REQUEST).message(violation.getMessage()).build();
                    errors.add(graphQLError);
                }
                return errors;
            });
        }
        return Mono.empty();
    }


}

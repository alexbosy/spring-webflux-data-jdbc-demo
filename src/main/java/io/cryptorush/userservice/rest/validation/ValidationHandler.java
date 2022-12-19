package io.cryptorush.userservice.rest.validation;

import io.cryptorush.userservice.domain.auth.AuthException;
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
import io.cryptorush.userservice.domain.validation.BusinessFieldValidationException;
import io.cryptorush.userservice.domain.validation.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class ValidationHandler {

    @Qualifier("rest-scheduler")
    private final Scheduler scheduler;

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<Map<String, String>>> handleRequestValidationException(WebExchangeBindException e) {
        return Mono.fromCallable(() -> {
            var errors = e.getBindingResult()
                    .getAllErrors()
                    .stream()
                    .collect(Collectors.toMap(objectError -> ((FieldError) objectError).getField(), objectError -> {
                        var msg = objectError.getDefaultMessage();
                        return msg != null ? msg : "Something went wrong";
                    }));
            return ResponseEntity.badRequest().body(errors);
        }).publishOn(scheduler);
    }

    @ExceptionHandler({BusinessFieldValidationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<ResponseEntity<Map<String, String>>> handleBusinessFieldValidationException(BusinessFieldValidationException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(e.getFieldName(), e.getMessage())))
                .publishOn(scheduler);
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ResponseEntity<Error>> handleNotFoundException(BusinessValidationException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(e.getMessage())))
                .publishOn(scheduler);
    }

    @ExceptionHandler({AuthException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ResponseEntity<Error>> handleAuthException(AuthException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(e.getMessage())))
                .publishOn(scheduler);
    }

    private record Error(String error) {
    }
}

package io.cryptorush.userservice.rest.validation;

import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
import io.cryptorush.userservice.domain.validation.BusinessFieldValidationException;
import io.cryptorush.userservice.domain.validation.BusinessValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidationHandler {

    private final Scheduler scheduler;

    public ValidationHandler(@Qualifier("rest-scheduler") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @ExceptionHandler(WebExchangeBindException.class)
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
    public Mono<ResponseEntity<Map<String, String>>> handleBusinessFieldValidationException(BusinessFieldValidationException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(e.getFieldName(), e.getMessage())))
                .publishOn(scheduler);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public Mono<ResponseEntity<Map<String, String>>> handleNotFoundException(BusinessValidationException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage())))
                .publishOn(scheduler);
    }
}
package io.cryptorush.userservice.domain.validation;

public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }
}

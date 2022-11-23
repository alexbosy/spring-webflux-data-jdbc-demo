package io.cryptorush.userservice.domain.validation;

public class InvalidUserTypeException extends BusinessValidationException {
    public InvalidUserTypeException() {
        super("type", "Forbidden user type");
    }
}

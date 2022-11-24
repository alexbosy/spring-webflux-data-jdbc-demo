package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessValidationException;

public class InvalidUserTypeException extends BusinessValidationException {
    public InvalidUserTypeException() {
        super("type", "Forbidden user type");
    }
}

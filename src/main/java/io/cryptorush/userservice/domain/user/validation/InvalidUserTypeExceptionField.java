package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessFieldValidationException;

public class InvalidUserTypeExceptionField extends BusinessFieldValidationException {
    public InvalidUserTypeExceptionField() {
        super("type", "Forbidden user type");
    }
}

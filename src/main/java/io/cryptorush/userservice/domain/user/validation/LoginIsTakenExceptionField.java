package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessFieldValidationException;

public class LoginIsTakenExceptionField extends BusinessFieldValidationException {
    public LoginIsTakenExceptionField() {
        super("login", "Supplied login is already taken");
    }
}

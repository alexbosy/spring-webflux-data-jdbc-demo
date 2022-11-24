package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessValidationException;

public class LoginIsTakenException extends BusinessValidationException {
    public LoginIsTakenException() {
        super("login", "Supplied login is already taken");
    }
}

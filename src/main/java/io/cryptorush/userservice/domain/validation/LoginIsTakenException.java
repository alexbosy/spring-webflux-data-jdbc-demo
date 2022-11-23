package io.cryptorush.userservice.domain.validation;

public class LoginIsTakenException extends BusinessValidationException {
    public LoginIsTakenException() {
        super("login", "Supplied login is already taken");
    }
}

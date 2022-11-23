package io.cryptorush.userservice.domain.validation;

public class EmailIsTakenException extends BusinessValidationException {
    public EmailIsTakenException() {
        super("email", "Supplied email is already taken");
    }
}

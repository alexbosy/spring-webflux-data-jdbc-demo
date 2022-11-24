package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessValidationException;

public class EmailIsTakenException extends BusinessValidationException {
    public EmailIsTakenException() {
        super("email", "Supplied email is already taken");
    }
}

package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessFieldValidationException;

public class EmailIsTakenExceptionField extends BusinessFieldValidationException {
    public EmailIsTakenExceptionField() {
        super("email", "Supplied email is already taken");
    }
}

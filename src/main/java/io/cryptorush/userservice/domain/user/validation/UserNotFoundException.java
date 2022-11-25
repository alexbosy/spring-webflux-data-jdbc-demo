package io.cryptorush.userservice.domain.user.validation;

import io.cryptorush.userservice.domain.validation.BusinessValidationException;

public class UserNotFoundException extends BusinessValidationException {
    public UserNotFoundException() {
        super("User not found");
    }
}

package io.cryptorush.userservice.domain.user;

import io.cryptorush.userservice.domain.user.validation.EmailIsTakenExceptionField;
import io.cryptorush.userservice.domain.user.validation.InvalidUserTypeExceptionField;
import io.cryptorush.userservice.domain.user.validation.LoginIsTakenExceptionField;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static io.cryptorush.userservice.domain.user.UserType.ADMIN;
import static io.cryptorush.userservice.domain.user.UserType.MANAGER;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(User user) {
        validateUserType(user);
        validateLoginAndEmail(user);
    }

    private void validateUserType(User user) {
        var type = user.getType();
        if (type != ADMIN && type != MANAGER) {
            throw new InvalidUserTypeExceptionField();
        }
    }

    private void validateLoginAndEmail(User user) {
        var currentLogin = user.getLogin();
        var currentEmail = user.getEmail();
        Optional<User> userOptional = userRepository.findByLoginOrEmail(currentLogin, currentEmail);
        userOptional.ifPresent(foundUser -> {
            var foundLogin = foundUser.getLogin();
            if (Objects.equals(foundLogin, currentLogin)) {
                throw new LoginIsTakenExceptionField();
            } else {
                throw new EmailIsTakenExceptionField();
            }
        });
    }
}
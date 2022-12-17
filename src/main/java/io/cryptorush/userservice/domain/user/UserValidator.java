package io.cryptorush.userservice.domain.user;

import io.cryptorush.userservice.domain.user.validation.EmailIsTakenExceptionField;
import io.cryptorush.userservice.domain.user.validation.InvalidUserTypeExceptionField;
import io.cryptorush.userservice.domain.user.validation.LoginIsTakenExceptionField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.cryptorush.userservice.domain.user.UserType.ADMIN;
import static io.cryptorush.userservice.domain.user.UserType.MANAGER;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateSystemUserCreationOrUpdate(User user) {
        validateUserType(user);
        validateForLoginAndEmail(user.getLogin(), user.getEmail(), user.getId());
    }

    public void validateCustomerUserCreationOrUpdate(User user) {
        validateForLoginAndEmail(user.getLogin(), user.getEmail(), user.getId());
    }

    private void validateUserType(User user) {
        var type = user.getType();
        if (type != ADMIN && type != MANAGER) {
            throw new InvalidUserTypeExceptionField();
        }
    }

    private void validateForLoginAndEmail(String currentLogin, String currentEmail, Long currentUserId) {
        Optional<User> userOptional = findAlreadyExistingUser(currentLogin, currentEmail, currentUserId);
        userOptional.ifPresent(foundUser -> {
            var foundLogin = foundUser.getLogin();
            if (Objects.equals(foundLogin, currentLogin)) {
                throw new LoginIsTakenExceptionField();
            } else {
                throw new EmailIsTakenExceptionField();
            }
        });
    }

    private Optional<User> findAlreadyExistingUser(String currentLogin, String currentEmail, Long currentUserId) {
        List<User> result;
        if (currentUserId != null) {
            result = userRepository.findByLoginOrEmailExceptId(currentLogin, currentEmail, currentUserId);
        } else {
            result = userRepository.findByLoginOrEmail(currentLogin, currentEmail);
        }
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}

package io.cryptorush.userservice.domain.user;

import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserService(UserRepository userRepository,
                              UserValidator userValidator,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(timeout = 1)
    public User createSystemUser(User user) {
        log.debug("Creating new system user=[{}]", user);
        userValidator.validateUserCreationOrUpdate(user);
        log.debug("Encoding password for user with login={}", user.getLogin());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Finished password encoding for user with login={}", user.getLogin());
        User createdUser = userRepository.save(user);
        log.debug("System user was created successfully, id=[{}]", createdUser.getId());
        return createdUser;
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void deleteById(long id) {
        long count = userRepository.hardDeleteById(id);
        if (count == 0) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public User updateUser(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            userValidator.validateUserCreationOrUpdate(user);
            User foundUser = optionalUser.get();
            foundUser.setLogin(user.getLogin());
            foundUser.setName(user.getName());
            foundUser.setSurname(user.getSurname());
            foundUser.setEmail(user.getEmail());
            foundUser.setType(user.getType());
            return userRepository.save(foundUser);
        } else {
            throw new UserNotFoundException();
        }
    }
}

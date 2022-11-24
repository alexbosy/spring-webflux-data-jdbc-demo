package io.cryptorush.userservice.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(timeout = 1)
    @Override
    public User createSystemUser(User user) {
        log.debug("Creating new system user=[{}]", user);
        userValidator.validate(user);
        log.debug("Encoding password for user with login={}", user.getLogin());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Finished password encoding for user with login={}", user.getLogin());
        User createdUser = userRepository.save(user);
        log.debug("System user was created successfully, id=[{}]", createdUser.getId());
        return createdUser;
    }
}

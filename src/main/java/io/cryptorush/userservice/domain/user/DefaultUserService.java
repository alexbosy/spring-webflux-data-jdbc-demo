package io.cryptorush.userservice.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public DefaultUserService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Override
    public User createSystemUser(User user) {
        log.debug("Creating new system user=[{}]", user);
        userValidator.validate(user);
        User createdUser = userRepository.save(user);
        log.debug("System user was created successfully, id=[{}]", createdUser.getId());
        return createdUser;
    }
}

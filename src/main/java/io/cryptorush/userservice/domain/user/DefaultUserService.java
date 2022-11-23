package io.cryptorush.userservice.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createSystemUser(User user) {
        log.debug("Creating new system user=[{}]", user);
        User createdUser = userRepository.save(user);
        log.debug("System user was created successfully, id=[{}]", createdUser.getId());
        return createdUser;
    }
}

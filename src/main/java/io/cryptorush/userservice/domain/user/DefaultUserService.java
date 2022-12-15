package io.cryptorush.userservice.domain.user;

import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    public final static int MAX_LIMIT = 100;

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(timeout = 1)
    public User createSystemUser(User user) {
        log.debug("Creating new system user=[{}]", user);
        userValidator.validateSystemUserCreationOrUpdate(user);
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
    public User getByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void deleteSystemUserById(long id) {
        long count = userRepository.hardDeleteById(id);
        if (count == 0) {
            throw new UserNotFoundException();
        }
    }

    @Override
    @Transactional(timeout = 1)
    public User updateUser(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            userValidator.validateSystemUserCreationOrUpdate(user);
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

    @Override
    public List<User> getAllSystemUsers(int offset, int limit) {
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }
        return userRepository.getAllSystemUsers(offset, limit);
    }

    @Override
    public List<User> getAllCustomerUsers(int offset, int limit) {
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }
        return userRepository.getAllCustomerUsers(offset, limit);
    }
}

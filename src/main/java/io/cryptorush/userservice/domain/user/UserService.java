package io.cryptorush.userservice.domain.user;

public interface UserService {
    User createSystemUser(User user);

    User getById(long id);
}

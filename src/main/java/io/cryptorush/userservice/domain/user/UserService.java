package io.cryptorush.userservice.domain.user;

import java.util.List;

public interface UserService {
    User createSystemUser(User user);

    User getById(long id);

    void deleteById(long id);

    User updateUser(User user);

    List<User> getAllSystemUsers(int offset, int limit);

    List<User> getAllCustomerUsers(int offset, int limit);
}

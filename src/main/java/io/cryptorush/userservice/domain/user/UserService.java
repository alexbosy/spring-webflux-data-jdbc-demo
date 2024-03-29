package io.cryptorush.userservice.domain.user;

import java.util.List;

public interface UserService {

    User createSystemUser(User user);

    User getById(long id);

    User getByLogin(String login);

    void deleteSystemUserById(long id);

    User updateSystemUser(User user);

    List<User> getAllSystemUsers(int offset, int limit);
}

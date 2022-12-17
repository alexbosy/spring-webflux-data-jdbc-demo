package io.cryptorush.userservice.domain.user;

import java.util.List;

public interface UserService {

    User createSystemUser(User user);

    User getById(long id);

    User getByLogin(String login);

    void deleteSystemUserById(long id);

    User updateUser(User user);

    List<User> getAllSystemUsers(int offset, int limit);

    List<User> getAllCustomerUsers(int offset, int limit); //TODO: consider moving this to customer service
}

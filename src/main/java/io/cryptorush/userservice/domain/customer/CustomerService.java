package io.cryptorush.userservice.domain.customer;

import io.cryptorush.userservice.domain.user.User;

public interface CustomerService {

    User registerNewCustomer(User user);

    User updateCustomerUser(String login, User user);

    User getCustomerUserByLogin(String login);

    void deleteCustomerUserByUserId(long userId);

    CustomerPublicProfile getCustomerPublicProfileByLogin(String login);
}

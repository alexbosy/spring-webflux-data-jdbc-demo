package io.cryptorush.userservice.domain.auth;

import io.cryptorush.userservice.domain.user.UserType;

public interface TokenService {

    String generateToken(String login, UserType type);
}

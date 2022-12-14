package io.cryptorush.userservice.domain.auth;

public interface AuthService {
    String authenticate(String login, String password);
}

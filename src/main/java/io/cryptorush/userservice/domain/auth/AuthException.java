package io.cryptorush.userservice.domain.auth;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("Wrong login or password");
    }
}

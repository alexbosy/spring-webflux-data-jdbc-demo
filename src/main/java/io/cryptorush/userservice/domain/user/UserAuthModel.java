package io.cryptorush.userservice.domain.user;

public record UserAuthModel(String password, UserType type) {
}

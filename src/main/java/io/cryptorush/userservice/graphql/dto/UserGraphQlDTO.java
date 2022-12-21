package io.cryptorush.userservice.graphql.dto;

import io.cryptorush.userservice.domain.user.UserType;

public record UserGraphQlDTO(
        long id,
        String login,
        String name,
        String surname,
        String email,
        UserType type) {
}

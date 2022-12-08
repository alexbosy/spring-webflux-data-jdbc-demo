package io.cryptorush.userservice.rest.user.dto;

import io.cryptorush.userservice.domain.user.UserType;

public record UserFullResponseDTO(
        long id,
        String login,
        String name,
        String surname,
        String email,
        UserType type) {
}

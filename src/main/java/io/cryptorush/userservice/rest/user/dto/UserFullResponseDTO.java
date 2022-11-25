package io.cryptorush.userservice.rest.user.dto;

import io.cryptorush.userservice.domain.user.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFullResponseDTO {
    private final long id;
    private final String login;
    private final String name;
    private final String surname;
    private final String email;
    private final UserType type;
}

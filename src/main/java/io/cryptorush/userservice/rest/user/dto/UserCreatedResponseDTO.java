package io.cryptorush.userservice.rest.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedResponseDTO {
    private final long id;
    private final String login;
}

package io.cryptorush.userservice.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedResponseDTO {
    private Long id;
    private String login;
}

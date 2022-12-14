package io.cryptorush.userservice.rest.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthRequestDTO {
    private String login;
    private String password;
}

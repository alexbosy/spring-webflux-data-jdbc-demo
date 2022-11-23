package io.cryptorush.userservice.rest.dto;

import io.cryptorush.userservice.domain.user.UserType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRequestDTO {
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserType type;
}

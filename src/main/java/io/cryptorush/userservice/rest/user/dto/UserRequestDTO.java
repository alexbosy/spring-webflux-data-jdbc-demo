package io.cryptorush.userservice.rest.user.dto;

import io.cryptorush.userservice.domain.user.UserType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRequestDTO {

    @Size.List({
            @Size(min = 6, message = "Login min length is {min} chars"),
            @Size(max = 20, message = "Login max length is {max} chars")
    })
    private String login;

    @NotBlank(message = "Name can not be empty")
    private String name;

    @NotBlank(message = "Surname can not be empty")
    private String surname;

    @NotBlank(message = "Email can not be empty")
    @Pattern(regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(.\\w{2,3})+$", message = "Email is not valid")
    private String email;

    @Size(min = 8, message = "Password min length is {min} chars")
    private String password;

    @NotNull(message = "Type can not be null")
    private UserType type;
}

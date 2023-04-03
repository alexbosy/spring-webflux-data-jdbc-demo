package io.cryptorush.userservice.rest.user.dto;

import io.cryptorush.userservice.domain.user.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {

    @NotBlank(message = "Login can not be empty")
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
    @Size(max = 25, message = "Email max length is {max} chars")
    private String email;

    @NotNull(message = "Type can not be empty")
    private UserType type;
}

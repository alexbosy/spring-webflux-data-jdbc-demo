package io.cryptorush.userservice.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class CustomerCreationRequestDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @NotBlank(message = "Date of birth can not be empty")
    private Date dateOfBirth;

    @NotBlank(message = "Country of residence can not be empty")
    @Size.List({
            @Size(min = 2, message = "Country of residence min length is {min} chars"),
            @Size(max = 2, message = "Country of residence max length is {max} chars")
    })
    private String countryOfResidence;

    @NotBlank(message = "Identity number can not be empty")
    private String identityNumber;

    @NotBlank(message = "Passport number can not be empty")
    private String passportNumber;


    @NotBlank(message = "Login can not be empty")
    @Size.List({
            @Size(min = 6, message = "Login min length is {min} chars"),
            @Size(max = 20, message = "Login max length is {max} chars")
    })
    private final String login;

    @NotBlank(message = "Name can not be empty")
    private final String name;

    @NotBlank(message = "Surname can not be empty")
    private final String surname;

    @NotBlank(message = "Email can not be empty")
    @Pattern(regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(.\\w{2,3})+$", message = "Email is not valid")
    @Size(max = 25, message = "Email max length is {max} chars")
    private final String email;

    @NotBlank(message = "Password can not be empty")
    @Size(min = 8, message = "Password min length is {min} chars")
    private final String password;
}
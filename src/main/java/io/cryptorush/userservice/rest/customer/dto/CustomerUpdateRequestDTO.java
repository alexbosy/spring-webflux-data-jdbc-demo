package io.cryptorush.userservice.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerUpdateRequestDTO {

    @NotBlank(message = "Name can not be empty")
    private String name;

    @NotBlank(message = "Surname can not be empty")
    private String surname;

    @NotBlank(message = "Email can not be empty")
    @Pattern(regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(.\\w{2,3})+$", message = "Email is not valid")
    @Size(max = 25, message = "Email max length is {max} chars")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth can not be empty")
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
}

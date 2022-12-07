package io.cryptorush.userservice.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CustomerFullProfileDTO {

    private final String login;
    private final String name;
    private final String surname;
    private final String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private final Date dateOfBirth;
    private final String countryOfResidence;
    private final String identityNumber;
    private final String passportNumber;
}

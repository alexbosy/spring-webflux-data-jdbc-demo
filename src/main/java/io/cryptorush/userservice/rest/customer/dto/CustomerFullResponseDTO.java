package io.cryptorush.userservice.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CustomerFullResponseDTO {

    private final long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private final Date dateOfBirth;
    private final String countryOfResidence;
    private final String identityNumber;
    private final String passportNumber;
    private final String registrationIp;
    private final String registrationCountry;

    private final long userId;
    private final String login;
    private final String name;
    private final String surname;
    private final String email;
}

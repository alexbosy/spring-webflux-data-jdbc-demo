package io.cryptorush.userservice.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record CustomerFullProfileDTO(
        String login,
        String name,
        String surname,
        String email,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        Date dateOfBirth,
        String countryOfResidence,
        String identityNumber,
        String passportNumber) {
}

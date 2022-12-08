package io.cryptorush.userservice.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record CustomerFullResponseDTO(
        long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        Date dateOfBirth,
        String countryOfResidence,
        String identityNumber,
        String passportNumber,
        String registrationIp,
        String registrationCountry,
        long userId,
        String login,
        String name,
        String surname,
        String email) {
}

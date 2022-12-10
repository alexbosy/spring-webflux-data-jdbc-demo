package io.cryptorush.userservice.domain.customer;

import java.util.Date;

public record CustomerPublicProfile(
        String login,
        String name,
        String surname,
        String email,
        Date dateOfBirth,
        String countryOfResidence) {
}
package io.cryptorush.userservice.domain.customer;

import io.cryptorush.userservice.domain.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Date;

@Data
public class Customer {
    @Id
    private Long id;
    private Date dateOfBirth;
    private String countryOfResidence;
    private String identityNumber;
    private String passportNumber;
    private String registrationIp;
    private String registrationCountry;
    @Column("user_id")
    private User user;
}

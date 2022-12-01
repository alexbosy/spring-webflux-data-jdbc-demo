package io.cryptorush.userservice.domain.customer;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.userdetails.User;

import java.util.Date;

@Data
@Builder
@Table("customers")
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
    private AggregateReference<User, Long> userRef;
}

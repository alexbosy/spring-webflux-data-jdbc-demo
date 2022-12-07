package io.cryptorush.userservice.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Builder
@Table("customers")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private Long id;
    private Date dateOfBirth;
    private String countryOfResidence;
    private String identityNumber;
    private String passportNumber;
    private String registrationIp;
    private String registrationCountry;
}

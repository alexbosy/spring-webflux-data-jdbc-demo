package io.cryptorush.userservice.domain.customer;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Modifying
    @Query("UPDATE customers SET registration_country=:pCountry WHERE id=:pId")
    void updateRegistrationCountry(@Param("pId") long id, @NotNull @Param("pCountry") String country);
}

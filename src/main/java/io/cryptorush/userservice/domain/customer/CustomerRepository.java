package io.cryptorush.userservice.domain.customer;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Modifying
    @Query("UPDATE customers SET registration_country=:pCountry WHERE id=:pId")
    void updateRegistrationCountry(@Param("pId") long id, @NotNull @Param("pCountry") String country);

    @Query("""
            SELECT u.*,
            cu.id as customer_id,
            cu.country_of_residence as customer_country_of_residence,
            cu.identity_number as customer_identity_number,
            cu.date_of_birth as customer_date_of_birth,
            cu.passport_number as customer_passport_number,
            cu.registration_ip as customer_registration_ip,
            cu.registration_country as customer_registration_country
            FROM users u, customers cu WHERE cu.user_id = u.id
            AND u.login=:pLogin AND u.type=:pType
            """)
    Optional<User> findCustomerUserByLoginAndType(@NonNull @Param("pLogin") String login,
                                                  @NonNull @Param("pType") UserType type);

    default Optional<User> findCustomerUserByLogin(@NonNull String login) {
        return findCustomerUserByLoginAndType(login, UserType.CUSTOMER);
    }

    @Modifying
    @Query("DELETE FROM customers WHERE user_id=:pUserId")
    long hardDeleteByUserId(@Param("pUserId") long userId);

    @Query("""
            SELECT u.login,
            u.name,
            u.surname,
            u.email,
            cu.date_of_birth,
            cu.country_of_residence
            FROM users u, customers cu
            WHERE cu.user_id = u.id
            AND u.login=:pLogin
            AND u.type='CUSTOMER'
            """)
    Optional<CustomerPublicProfile> findCustomerPublicProfileByLogin(@NonNull @Param("pLogin") String login);

    @Query("""
            SELECT u.*,
            cu.id as customer_id,
            cu.country_of_residence as customer_country_of_residence,
            cu.identity_number as customer_identity_number,
            cu.date_of_birth as customer_date_of_birth,
            cu.passport_number as customer_passport_number,
            cu.registration_ip as customer_registration_ip,
            cu.registration_country as customer_registration_country
            FROM users u, customers cu WHERE cu.user_id = u.id
            ORDER BY u.id OFFSET :pOffset LIMIT :pLimit
            """)
    List<User> getAllCustomerUsers(@Param("pOffset") int offset, @Param("pLimit") int limit);
}

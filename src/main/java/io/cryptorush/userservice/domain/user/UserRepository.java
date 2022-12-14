package io.cryptorush.userservice.domain.user;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT password,type FROM users WHERE login=:pLogin")
    Optional<UserAuthModel> findUserAuthModelByLogin(@NonNull @Param("pLogin") String login);

    @Query("SELECT * FROM users WHERE login=:pLogin OR email=:pEmail")
    Optional<User> findByLoginOrEmail(@NonNull @Param("pLogin") String login, @NonNull @Param("pEmail") String email);

    @Query("SELECT * FROM users WHERE (login=:pLogin OR email=:pEmail) AND id!=:pId")
    Optional<User> findByLoginOrEmailExceptId(@NonNull @Param("pLogin") String login,
                                              @NonNull @Param("pEmail") String email, @NonNull @Param("pId") long id);

    @Query("SELECT * FROM users WHERE id=:pId")
    Optional<User> findById(@Param("pId") long id);

    @Modifying
    @Query("DELETE FROM users WHERE id=:pId")
    long hardDeleteById(@Param("pId") long id);

    @Query("SELECT * FROM users WHERE type!=:pType ORDER BY id OFFSET :pOffset LIMIT :pLimit")
    List<User> getUsersExceptOfType(@NonNull @Param("pType") UserType type, @Param("pOffset") int offset,
                                    @Param("pLimit") int limit);

    default List<User> getAllSystemUsers(int offset, int limit) {
        return getUsersExceptOfType(UserType.CUSTOMER, offset, limit);
    }

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

package io.cryptorush.userservice.domain.user;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT * FROM users WHERE login=:pLogin OR email=:pEmail")
    Optional<User> findByLoginOrEmail(@NonNull @Param("pLogin") String login, @NonNull @Param("pEmail") String email);

    @Query("SELECT * FROM users WHERE id=:pId")
    Optional<User> findById(@Param("pId") long id);
}

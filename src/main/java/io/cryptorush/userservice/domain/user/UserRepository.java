package io.cryptorush.userservice.domain.user;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

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

    @Query("SELECT * FROM users ORDER BY id OFFSET :pOffset LIMIT :pLimit ")
    List<User> getAllUsers(@Param("pOffset") int offset, @Param("pLimit") int limit);
}

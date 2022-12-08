package io.cryptorush.userservice.domain.user;

import io.cryptorush.userservice.domain.customer.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("users")
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserType type;
    @Column("user_id")
    private Customer customer;
}

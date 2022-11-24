package io.cryptorush.userservice.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserType type;
}

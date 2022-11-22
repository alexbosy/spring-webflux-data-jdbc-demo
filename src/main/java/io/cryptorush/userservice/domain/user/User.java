package io.cryptorush.userservice.domain.user;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {
    @Id
    private Long id;
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
}

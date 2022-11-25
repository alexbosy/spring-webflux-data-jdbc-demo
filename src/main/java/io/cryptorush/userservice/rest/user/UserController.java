package io.cryptorush.userservice.rest.user;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.rest.user.dto.UserCreatedResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserCreationRequestDTO;
import io.cryptorush.userservice.rest.user.dto.UserFullResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserUpdateRequestDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.Valid;

@RestController
@RequestMapping("user")
public class UserController {

    private final Scheduler scheduler;
    private final UserService userService;

    public UserController(@Qualifier("rest-scheduler") Scheduler scheduler, UserService userService) {
        this.scheduler = scheduler;
        this.userService = userService;
    }

    @PostMapping
    public Mono<UserCreatedResponseDTO> createUser(@Valid @RequestBody UserCreationRequestDTO userCreationRequestDTO) {
        return Mono.fromCallable(() -> {
            User user = User.builder()
                    .login(userCreationRequestDTO.getLogin())
                    .name(userCreationRequestDTO.getName())
                    .surname(userCreationRequestDTO.getSurname())
                    .email(userCreationRequestDTO.getEmail())
                    .password(userCreationRequestDTO.getPassword())
                    .type(userCreationRequestDTO.getType())
                    .build();

            User createdUser = userService.createSystemUser(user);

            return UserCreatedResponseDTO.builder()
                    .id(createdUser.getId())
                    .login(createdUser.getLogin())
                    .build();
        }).publishOn(scheduler);
    }

    @PutMapping("/{id}")
    public Mono<UserFullResponseDTO> updateUser(@PathVariable("id") long id,
                                                @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return Mono.fromCallable(() -> {
            User user = User.builder()
                    .id(id)
                    .login(userUpdateRequestDTO.getLogin())
                    .name(userUpdateRequestDTO.getName())
                    .surname(userUpdateRequestDTO.getSurname())
                    .email(userUpdateRequestDTO.getEmail())
                    .type(userUpdateRequestDTO.getType())
                    .build();
            User updatedUser = userService.updateUser(user);
            return UserFullResponseDTO.builder()
                    .id(updatedUser.getId())
                    .login(updatedUser.getLogin())
                    .name(updatedUser.getName())
                    .surname(updatedUser.getSurname())
                    .email(updatedUser.getEmail())
                    .type(updatedUser.getType()).build();
        }).publishOn(scheduler);
    }

    @GetMapping("/{id}")
    public Mono<UserFullResponseDTO> getUser(@PathVariable("id") long id) {
        return Mono.fromCallable(() -> {
            User user = userService.getById(id);
            return UserFullResponseDTO.builder()
                    .id(user.getId())
                    .login(user.getLogin())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .type(user.getType()).build();
        }).publishOn(scheduler);
    }

    @DeleteMapping(path = "/{id}")
    Mono<ResponseEntity<Object>> deleteUser(@PathVariable("id") long id) {
        return Mono.fromCallable(() -> {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }).publishOn(scheduler);
    }
}
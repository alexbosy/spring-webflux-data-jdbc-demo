package io.cryptorush.userservice.rest.user;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.rest.user.dto.UserCreatedResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserCreationRequestDTO;
import io.cryptorush.userservice.rest.user.dto.UserFullResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserUpdateRequestDTO;
import io.cryptorush.userservice.rest.user.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final Scheduler scheduler;
    private final UserService userService;
    private final SystemUserMapper userMapper;

    public UserController(@Qualifier("rest-scheduler") Scheduler scheduler, UserService userService, SystemUserMapper systemUserMapper) {
        this.scheduler = scheduler;
        this.userService = userService;
        this.userMapper = systemUserMapper;
    }

    @PostMapping("user")
    public Mono<UserCreatedResponseDTO> createUser(@Valid @RequestBody UserCreationRequestDTO userCreationRequestDTO) {
        return Mono.fromCallable(() -> {
            User user = userMapper.toUser(userCreationRequestDTO);
            User createdUser = userService.createSystemUser(user);
            return userMapper.toCreatedResponseDTO(createdUser);
        }).publishOn(scheduler);
    }

    @PutMapping("user/{id}")
    public Mono<UserFullResponseDTO> updateUser(@PathVariable("id") long id,
                                                @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return Mono.fromCallable(() -> {
            User user = userMapper.toUser(id, userUpdateRequestDTO);
            User updatedUser = userService.updateUser(user);
            return userMapper.toFullResponseDTO(updatedUser);
        }).publishOn(scheduler);
    }

    @GetMapping("user/{id}")
    public Mono<UserFullResponseDTO> getUser(@PathVariable("id") long id) {
        return Mono.fromCallable(() -> {
            User user = userService.getById(id);
            return userMapper.toFullResponseDTO(user);
        }).publishOn(scheduler);
    }

    @DeleteMapping("user/{id}")
    Mono<ResponseEntity<Object>> deleteUser(@PathVariable("id") long id) {
        return Mono.fromCallable(() -> {
            userService.deleteSystemUserById(id);
            return ResponseEntity.noContent().build();
        }).publishOn(scheduler);
    }

    @GetMapping("users")
    Mono<List<UserFullResponseDTO>> getUsers(@RequestParam(defaultValue = "0", required = false) int offset,
                                             @RequestParam(defaultValue = "10", required = false) int limit) {
        return Mono.fromCallable(() -> {
            List<User> users = userService.getAllSystemUsers(offset, limit);
            return users.stream().map(userMapper::toFullResponseDTO).collect(Collectors.toList());
        }).publishOn(scheduler);
    }
}
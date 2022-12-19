package io.cryptorush.userservice.rest.user;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.rest.user.dto.UserCreatedResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserCreationRequestDTO;
import io.cryptorush.userservice.rest.user.dto.UserFullResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserUpdateRequestDTO;
import io.cryptorush.userservice.rest.user.mapper.SystemUserMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    @Qualifier("rest-scheduler")
    private final Scheduler scheduler;
    private final UserService userService;
    private final SystemUserMapper userMapper;

    @PostMapping("user")
    public Mono<UserCreatedResponseDTO> createUser(@Valid @RequestBody UserCreationRequestDTO userCreationRequestDTO) {
        return Mono.fromCallable(() -> {
            User user = userMapper.toUser(userCreationRequestDTO);
            User createdUser = userService.createSystemUser(user);
            return userMapper.toCreatedResponseDTO(createdUser);
        }).publishOn(scheduler);
    }

    @PutMapping("user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "jwt")
    public Mono<UserFullResponseDTO> updateUser(@PathVariable("id") long id,
                                                @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return Mono.fromCallable(() -> {
            User user = userMapper.toUser(id, userUpdateRequestDTO);
            User updatedUser = userService.updateSystemUser(user);
            return userMapper.toFullResponseDTO(updatedUser);
        }).publishOn(scheduler);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserFullResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    public Mono<UserFullResponseDTO> getUser(@PathVariable("id") long id) {
        return Mono.fromCallable(() -> {
            User user = userService.getById(id);
            return userMapper.toFullResponseDTO(user);
        }).publishOn(scheduler);
    }

    @GetMapping("me")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') OR hasAuthority('SCOPE_MANAGER')")
    @SecurityRequirement(name = "jwt")
    public Mono<UserFullResponseDTO> getMe(Principal principal) {
        return Mono.fromCallable(() -> {
            User user = userService.getByLogin(principal.getName());
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
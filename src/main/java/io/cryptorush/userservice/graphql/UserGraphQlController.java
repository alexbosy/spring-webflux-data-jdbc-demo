package io.cryptorush.userservice.graphql;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.graphql.dto.SystemUserGraphQlInputDTO;
import io.cryptorush.userservice.graphql.dto.UserGraphQlDTO;
import io.cryptorush.userservice.graphql.mapper.UserGqlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserGraphQlController {

    @Qualifier("graphql-scheduler")
    private final Scheduler scheduler;
    private final UserService userService;
    private final UserGqlMapper userGqlMapper;

    @QueryMapping
    public Mono<List<UserGraphQlDTO>> allUsers(@Argument int offset, @Argument int limit) {
        return Mono.fromCallable(() -> {
            List<User> users = userService.getAllSystemUsers(offset, limit);
            return users.stream()
                    .map(userGqlMapper::toGraphQlDTO)
                    .collect(Collectors.toList());
        }).publishOn(scheduler);
    }

    @QueryMapping
    public Mono<UserGraphQlDTO> userById(@Argument long id) {
        return Mono.fromCallable(() -> {
            User user = userService.getById(id);
            return userGqlMapper.toGraphQlDTO(user);
        }).publishOn(scheduler);
    }

    @MutationMapping("createUser")
    public Mono<UserGraphQlDTO> createSystemUser(@Argument("systemUserInput") @Valid SystemUserGraphQlInputDTO systemUserDTO) {
        return Mono.fromCallable(() -> {
            User user = userService.createSystemUser(userGqlMapper.toUser(systemUserDTO));
            return userGqlMapper.toGraphQlDTO(user);
        }).publishOn(scheduler);
    }
}

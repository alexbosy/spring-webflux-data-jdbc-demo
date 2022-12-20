package io.cryptorush.userservice.graphql;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.graphql.dto.UserGqlDTO;
import io.cryptorush.userservice.graphql.mapper.UserGqlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserGraphQlController {

    @Qualifier("rest-scheduler")
    private final Scheduler scheduler;
    private final UserService userService;
    private final UserGqlMapper userGqlMapper;

    @QueryMapping
    public Mono<List<UserGqlDTO>> allUsers(@Argument int offset, @Argument int limit) {
        return Mono.fromCallable(() -> {
            List<User> users = userService.getAllSystemUsers(offset, limit);
            return users.stream()
                    .map(userGqlMapper::toGqlDTO)
                    .collect(Collectors.toList());
        }).publishOn(scheduler);
    }

    @QueryMapping
    public Mono<UserGqlDTO> userById(@Argument int id) {
        return Mono.fromCallable(() -> {
            User user = userService.getById(id);
            return userGqlMapper.toGqlDTO(user);
        }).publishOn(scheduler);
    }
}

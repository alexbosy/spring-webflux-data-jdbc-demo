package io.cryptorush.userservice.rest.auth;

import io.cryptorush.userservice.domain.auth.AuthService;
import io.cryptorush.userservice.rest.auth.dto.AuthRequestDTO;
import io.cryptorush.userservice.rest.auth.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Qualifier("rest-scheduler")
    private final Scheduler scheduler;
    private final AuthService authService;

    @PostMapping("/auth")
    public Mono<AuthResponseDTO> auth(@RequestBody AuthRequestDTO requestDTO) {
        return Mono.fromCallable(() -> {
            String token = authService.authenticate(requestDTO.getLogin(), requestDTO.getPassword());
            return new AuthResponseDTO(token);
        }).publishOn(scheduler);
    }
}

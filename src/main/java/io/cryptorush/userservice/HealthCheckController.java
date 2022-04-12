package io.cryptorush.userservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HealthCheckController {

    @RequestMapping("/check")
    public Mono<String> check() {
        return Mono.just("User Service:[OK]");
    }
}
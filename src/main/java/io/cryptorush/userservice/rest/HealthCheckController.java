package io.cryptorush.userservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @Qualifier("rest-scheduler")
    private final Scheduler scheduler;

    @RequestMapping("/check")
    public Mono<String> check() {
        return Mono.just("user-service:[OK]").publishOn(scheduler);
    }
}
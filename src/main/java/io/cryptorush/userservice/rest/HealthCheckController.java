package io.cryptorush.userservice.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class HealthCheckController {

    private final Scheduler scheduler;

    public HealthCheckController(@Qualifier("rest-scheduler") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @RequestMapping("/check")
    public Mono<String> check() {
        return Mono.just("user-service:[OK]").publishOn(scheduler);
    }
}
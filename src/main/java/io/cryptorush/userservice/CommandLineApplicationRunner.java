package io.cryptorush.userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CommandLineApplicationRunner implements CommandLineRunner {

    private final Environment environment;

    @Autowired
    public CommandLineApplicationRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        List<String> defaultProfiles = Arrays.asList(environment.getDefaultProfiles());
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        log.info("User Service is started with the following spring profiles: default={} active={}", defaultProfiles, activeProfiles);
    }
}

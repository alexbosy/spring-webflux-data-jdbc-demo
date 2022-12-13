package io.cryptorush.userservice.domain.geoip;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalGeoIpService implements GeoIpService {

    public static final String UNKNOWN_COUNTRY_CODE = "XX";
    public static final String FALLBACK_COUNTRY_CODE = "ZZ";
    public static final String REQUEST_URI_PATTERN = "/json/%s";

    @Qualifier("ext-scheduler")
    private final Scheduler scheduler;
    private final WebClient webClient;

    @Override
    @CircuitBreaker(name = "external-geoip-service", fallbackMethod = "fallback")
    public Mono<String> getCountryCodeByIp(String ip) {
        log.debug("Resolving country for IP=[{}]", ip);
        return webClient.get()
                .uri(REQUEST_URI_PATTERN.formatted(ip))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(GeoIpResponse.class)
                                .map(result -> {
                                    String countryCode = result.country_code;
                                    return StringUtils.hasText(countryCode) ? countryCode : UNKNOWN_COUNTRY_CODE;
                                });
                    } else {
                        return Mono.just(UNKNOWN_COUNTRY_CODE);
                    }
                })
                .doOnSuccess(country -> log.debug("Country for IP=[{}] is [{}]", ip, country))
                .publishOn(scheduler);
    }

    private record GeoIpResponse(String country_code) {
    }

    private Mono<String> fallback(Exception e) {
        return Mono.just(FALLBACK_COUNTRY_CODE);
    }
}
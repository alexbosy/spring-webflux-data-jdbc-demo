package io.cryptorush.userservice.domain.geoip;

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
public class ExternalGeoIpService implements GeoIpService {

    public static final String UNKNOWN_COUNTRY_CODE = "XX";
    public static final String REQUEST_URI_PATTERN = "/json/%s";

    private final WebClient webClient;
    private final Scheduler scheduler;

    public ExternalGeoIpService(WebClient webClient,
                                @Qualifier("ext-scheduler") Scheduler scheduler) {

        this.webClient = webClient;
        this.scheduler = scheduler;
    }

    //Sample request - GET https://reallyfreegeoip.org/json/88.23.45.55
    @Override
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
}
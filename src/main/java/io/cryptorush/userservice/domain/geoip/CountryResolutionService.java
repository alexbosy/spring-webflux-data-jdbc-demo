package io.cryptorush.userservice.domain.geoip;

import reactor.core.publisher.Mono;

public interface CountryResolutionService {
    Mono<String> getCountryCodeByIp(String registrationIp);
}

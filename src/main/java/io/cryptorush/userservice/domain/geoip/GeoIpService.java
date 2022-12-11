package io.cryptorush.userservice.domain.geoip;

import reactor.core.publisher.Mono;

public interface GeoIpService {
    Mono<String> getCountryCodeByIp(String registrationIp);
}

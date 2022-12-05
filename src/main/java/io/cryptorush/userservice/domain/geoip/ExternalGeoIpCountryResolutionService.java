package io.cryptorush.userservice.domain.geoip;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class ExternalGeoIpCountryResolutionService implements CountryResolutionService {

    public static final String EXTERNAL_GEOIP_SERVICE_URL = "https://reallyfreegeoip.org";
    public static final String UNKNOWN_COUNTRY_CODE = "XX";
    public static final String REQUEST_PATTERN = "/json/%s";

    private final WebClient webClient;

    public ExternalGeoIpCountryResolutionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(EXTERNAL_GEOIP_SERVICE_URL).build();
    }

    //Example query - GET https://reallyfreegeoip.org/json/88.23.45.55
    @Override
    public String getCountryCodeByIp(String registrationIp) {
        String uri = REQUEST_PATTERN.formatted(registrationIp);
        Mono<String> geoIpResponseMono = webClient.get().uri(uri).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(GeoIpResponse.class)
                        .map(result -> result.country_code);
            } else {
                return Mono.just(UNKNOWN_COUNTRY_CODE);
            }
        });
        return Objects.requireNonNull(geoIpResponseMono.block());
    }

    private record GeoIpResponse(String country_code) {
    }
}





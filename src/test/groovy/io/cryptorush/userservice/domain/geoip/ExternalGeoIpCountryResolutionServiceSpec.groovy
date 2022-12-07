package io.cryptorush.userservice.domain.geoip

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class ExternalGeoIpCountryResolutionServiceSpec extends Specification {

    def exchangeFunction = Mock(ExchangeFunction)
    def webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction)
    def scheduler = Schedulers.immediate()
    def service = new ExternalGeoIpCountryResolutionService(webClientBuilder, scheduler)

    def "resolve country code for supplied IP, when external service returns HTTP success result"() {
        given:
        ClientResponse response = ClientResponse.create(httpStatus)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{ \"country_code\": \"${returnedCountryCode}\" }").build()
        exchangeFunction.exchange(_) >> Mono.just(response)

        when:
        def res = service.getCountryCodeByIp("any IP").block()

        then:
        res == expectedResult

        where:
        httpStatus             | returnedCountryCode || expectedResult
        HttpStatus.OK          | "LV"                || "LV"
        HttpStatus.OK          | ""                  || ExternalGeoIpCountryResolutionService.UNKNOWN_COUNTRY_CODE
        HttpStatus.BAD_REQUEST | ""                  || ExternalGeoIpCountryResolutionService.UNKNOWN_COUNTRY_CODE
    }
}

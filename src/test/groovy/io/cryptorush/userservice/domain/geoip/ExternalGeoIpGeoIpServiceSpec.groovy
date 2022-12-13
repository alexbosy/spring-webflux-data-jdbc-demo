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

class ExternalGeoIpGeoIpServiceSpec extends Specification {

    def exchangeFunction = Mock(ExchangeFunction)
    def webClient = WebClient.builder().exchangeFunction(exchangeFunction).build()
    def scheduler = Schedulers.immediate()
    def service = new ExternalGeoIpService(scheduler, webClient)

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
        HttpStatus.OK          | ""                  || ExternalGeoIpService.UNKNOWN_COUNTRY_CODE
        HttpStatus.BAD_REQUEST | ""                  || ExternalGeoIpService.UNKNOWN_COUNTRY_CODE
    }
}

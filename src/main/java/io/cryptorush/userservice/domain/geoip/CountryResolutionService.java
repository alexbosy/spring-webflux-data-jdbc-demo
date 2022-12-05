package io.cryptorush.userservice.domain.geoip;

public interface CountryResolutionService {
    String getCountryCodeByIp(String registrationIp);
}

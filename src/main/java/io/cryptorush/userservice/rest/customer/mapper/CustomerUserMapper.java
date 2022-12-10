package io.cryptorush.userservice.rest.customer.mapper;

import io.cryptorush.userservice.domain.customer.CustomerPublicProfile;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserType;
import io.cryptorush.userservice.rest.customer.dto.CustomerCreationRequestDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullProfileDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullResponseDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerPublicProfileDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerUserMapper {

    @Mapping(target = "customer.dateOfBirth", source = "dto.dateOfBirth")
    @Mapping(target = "customer.countryOfResidence", source = "dto.countryOfResidence")
    @Mapping(target = "customer.identityNumber", source = "dto.identityNumber")
    @Mapping(target = "customer.passportNumber", source = "dto.passportNumber")
    @Mapping(target = "customer.registrationIp", source = "ip")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "surname", source = "dto.surname")
    @Mapping(target = "password", source = "dto.password")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "id", ignore = true)
    User toCustomer(String ip, UserType type, CustomerCreationRequestDTO dto);

    @Mapping(target = "dateOfBirth", source = "user.customer.dateOfBirth")
    @Mapping(target = "countryOfResidence", source = "user.customer.countryOfResidence")
    @Mapping(target = "identityNumber", source = "user.customer.identityNumber")
    @Mapping(target = "passportNumber", source = "user.customer.passportNumber")
    CustomerFullProfileDTO toFullProfileDTO(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "id", source = "user.customer.id")
    @Mapping(target = "dateOfBirth", source = "user.customer.dateOfBirth")
    @Mapping(target = "countryOfResidence", source = "user.customer.countryOfResidence")
    @Mapping(target = "identityNumber", source = "user.customer.identityNumber")
    @Mapping(target = "passportNumber", source = "user.customer.passportNumber")
    @Mapping(target = "registrationIp", source = "user.customer.registrationIp")
    @Mapping(target = "registrationCountry", source = "user.customer.registrationCountry")
    CustomerFullResponseDTO toFullResponseDTO(User user);

    CustomerPublicProfileDTO toCustomerPublicProfileDTO(CustomerPublicProfile customerPublicProfile);
}

package io.cryptorush.userservice.rest.customer;

import io.cryptorush.userservice.domain.customer.Customer;
import io.cryptorush.userservice.domain.customer.CustomerService;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.domain.user.UserType;
import io.cryptorush.userservice.rest.customer.dto.CustomerCreationRequestDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullProfileDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullResponseDTO;
import io.cryptorush.userservice.rest.util.IpResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomerController {

    private final Scheduler scheduler;
    private final UserService userService;
    private final CustomerService customerService;
    private final IpResolver ipResolver;

    public CustomerController(@Qualifier("rest-scheduler") Scheduler scheduler, UserService userService,
                              CustomerService customerService, IpResolver ipResolver) {
        this.scheduler = scheduler;
        this.userService = userService;
        this.customerService = customerService;
        this.ipResolver = ipResolver;
    }

    @PostMapping("customer/registration")
    Mono<CustomerFullProfileDTO> registerNewCustomer(@Valid @RequestBody CustomerCreationRequestDTO requestDTO, ServerHttpRequest request) {
        return Mono.fromCallable(() -> {

            String ip = ipResolver.resolveIpAddress(request.getRemoteAddress());
            Customer customer = Customer.builder()
                    .dateOfBirth(requestDTO.getDateOfBirth())
                    .countryOfResidence(requestDTO.getCountryOfResidence())
                    .identityNumber(requestDTO.getIdentityNumber())
                    .passportNumber(requestDTO.getPassportNumber())
                    .registrationIp(ip)
                    .build();

            User user = User.builder()
                    .type(UserType.CUSTOMER)
                    .login(requestDTO.getLogin())
                    .name(requestDTO.getName())
                    .surname(requestDTO.getSurname())
                    .password(requestDTO.getPassword())
                    .email(requestDTO.getEmail())
                    .customer(customer)
                    .build();

            User registeredUser = customerService.registerNewCustomer(user);

            return CustomerFullProfileDTO.builder()
                    .login(registeredUser.getLogin())
                    .name(registeredUser.getName())
                    .surname(registeredUser.getSurname())
                    .email(registeredUser.getEmail()).dateOfBrith(registeredUser.getCustomer().getDateOfBirth())
                    .countryOfResidence(registeredUser.getCustomer().getCountryOfResidence())
                    .identityNumber(registeredUser.getCustomer().getIdentityNumber())
                    .passportNumber(registeredUser.getCustomer().getPassportNumber())
                    .build();
        }).publishOn(scheduler);

    }

    @GetMapping("customers")
    Mono<List<CustomerFullResponseDTO>> getCustomers(@RequestParam(defaultValue = "0", required = false) int offset,
                                                     @RequestParam(defaultValue = "10", required = false) int limit) {
        return Mono.fromCallable(() -> {
            List<User> users = userService.getAllCustomerUsers(offset, limit);
            return users.stream().map(user -> {
                Customer customer = user.getCustomer();
                return CustomerFullResponseDTO.builder()
                        .userId(user.getId())
                        .login(user.getLogin())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .email(user.getEmail())
                        .id(customer.getId())
                        .dateOfBirth(customer.getDateOfBirth())
                        .countryOfResidence(customer.getCountryOfResidence())
                        .identityNumber(customer.getIdentityNumber())
                        .passportNumber(customer.getPassportNumber())
                                .registrationIp(customer.getRegistrationIp())
                                .registrationCountry(customer.getRegistrationCountry())
                                .build();
                    }
            ).collect(Collectors.toList());
        }).publishOn(scheduler);
    }
}

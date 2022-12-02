package io.cryptorush.userservice.rest.customer;

import io.cryptorush.userservice.domain.customer.Customer;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomerController {

    private final Scheduler scheduler;
    private final UserService userService;

    public CustomerController(Scheduler scheduler, UserService userService) {
        this.scheduler = scheduler;
        this.userService = userService;
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

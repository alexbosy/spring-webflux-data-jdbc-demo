package io.cryptorush.userservice.rest.customer;

import io.cryptorush.userservice.domain.customer.CustomerPublicProfile;
import io.cryptorush.userservice.domain.customer.CustomerService;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserService;
import io.cryptorush.userservice.domain.user.UserType;
import io.cryptorush.userservice.rest.customer.dto.CustomerCreationRequestDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullProfileDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerFullResponseDTO;
import io.cryptorush.userservice.rest.customer.dto.CustomerPublicProfileDTO;
import io.cryptorush.userservice.rest.customer.mapper.CustomerUserMapper;
import io.cryptorush.userservice.rest.util.IpResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class CustomerController {

    private final Scheduler scheduler;
    private final UserService userService;
    private final CustomerService customerService;
    private final IpResolver ipResolver;
    private final CustomerUserMapper customerUserMapper;

    public CustomerController(@Qualifier("rest-scheduler") Scheduler scheduler, UserService userService,
                              CustomerService customerService, IpResolver ipResolver, CustomerUserMapper customerUserMapper) {
        this.scheduler = scheduler;
        this.userService = userService;
        this.customerService = customerService;
        this.ipResolver = ipResolver;
        this.customerUserMapper = customerUserMapper;
    }

    @PostMapping("customer/registration")
    Mono<CustomerFullProfileDTO> registerNewCustomer(@Valid @RequestBody CustomerCreationRequestDTO requestDTO, ServerHttpRequest request) {
        return Mono.fromCallable(() -> {
            String ip = ipResolver.resolveIpAddress(request.getRemoteAddress());
            User user = customerUserMapper.toCustomer(ip, UserType.CUSTOMER, requestDTO);
            User registeredUser = customerService.registerNewCustomer(user);
            return customerUserMapper.toFullProfileDTO(registeredUser);
        }).publishOn(scheduler);
    }

    @GetMapping("customers")
    Mono<List<CustomerFullResponseDTO>> getCustomers(@RequestParam(defaultValue = "0", required = false) int offset,
                                                     @RequestParam(defaultValue = "10", required = false) int limit) {
        return Mono.fromCallable(() -> {
            List<User> users = userService.getAllCustomerUsers(offset, limit);
            return users.stream().map(customerUserMapper::toFullResponseDTO).collect(Collectors.toList());
        }).publishOn(scheduler);
    }

    @GetMapping("customer/{login}")
    Mono<CustomerFullResponseDTO> getCustomer(@PathVariable("login") String login) {
        return Mono.fromCallable(() -> {
            User user = customerService.getCustomerUserByLogin(login);
            return customerUserMapper.toFullResponseDTO(user);
        }).publishOn(scheduler);
    }

    @GetMapping("customer/profile/{login}")
    Mono<CustomerPublicProfileDTO> getCustomerPublicProfile(@PathVariable("login") String login) {
        return Mono.fromCallable(() -> {
            CustomerPublicProfile profile = customerService.getCustomerPublicProfileByLogin(login);
            return customerUserMapper.toCustomerPublicProfileDTO(profile);
        }).publishOn(scheduler);
    }

    @DeleteMapping("customer/{userId}")
    Mono<ResponseEntity<Object>> deleteUser(@PathVariable("userId") long userId) {
        return Mono.fromCallable(() -> {
            customerService.deleteCustomerUserByUserId(userId);
            return ResponseEntity.noContent().build();
        }).publishOn(scheduler);
    }
}

package io.cryptorush.userservice.domain.customer;

import io.cryptorush.userservice.domain.geoip.CountryResolutionService;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserRepository;
import io.cryptorush.userservice.domain.user.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DefaultCustomerService implements CustomerService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final CountryResolutionService countryResolutionService;

    public DefaultCustomerService(UserRepository userRepository, UserValidator userValidator,
                                  PasswordEncoder passwordEncoder, CountryResolutionService countryResolutionService) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.countryResolutionService = countryResolutionService;
    }

    @Transactional(timeout = 2)
    @Override
    public User registerNewCustomer(User user) {
        log.debug("Creating new customer user=[{}]", user);
        userValidator.validateCustomerUserCreationOrUpdate(user);

        log.debug("Encoding password for customer user with login={}", user.getLogin());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Finished password encoding for customer user with login={}", user.getLogin());

        String registrationIp = user.getCustomer().getRegistrationIp();
        log.debug("Resolving country of registration by IP=[{}]", registrationIp);
        String registrationCountry = countryResolutionService.getCountryCodeByIp(registrationIp);
        user.getCustomer().setRegistrationCountry(registrationCountry);
        log.debug("Country of registration for IP=[{}] is [{}]", registrationIp, registrationCountry);

        User createdCustomerUser = userRepository.save(user);
        log.debug("Customer user was created successfully, id=[{}]", createdCustomerUser.getId());
        return createdCustomerUser;
    }
}

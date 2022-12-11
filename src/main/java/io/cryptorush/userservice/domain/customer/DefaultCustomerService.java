package io.cryptorush.userservice.domain.customer;

import io.cryptorush.userservice.domain.geoip.GeoIpService;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserRepository;
import io.cryptorush.userservice.domain.user.UserValidator;
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
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
    private final GeoIpService geoIpService;
    private final CustomerRepository customerRepository;

    public DefaultCustomerService(UserRepository userRepository, UserValidator userValidator,
                                  PasswordEncoder passwordEncoder, GeoIpService geoIpService,
                                  CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.geoIpService = geoIpService;
        this.customerRepository = customerRepository;
    }

    @Transactional(timeout = 2)
    @Override
    public User registerNewCustomer(User user) {
        log.debug("Creating new customer user=[{}]", user);
        userValidator.validateCustomerUserCreationOrUpdate(user);
        log.debug("Encoding password for customer user with login={}", user.getLogin());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Finished password encoding for customer user with login={}", user.getLogin());
        User createdCustomerUser = userRepository.save(user);
        log.debug("Customer user was created successfully, id=[{}]", createdCustomerUser.getId());

        geoIpService.getCountryCodeByIp(user.getCustomer().getRegistrationIp())
                .subscribe(country -> {
                    Long customerId = createdCustomerUser.getCustomer().getId();
                    customerRepository.updateRegistrationCountry(customerId, country);
                    log.debug("Customer user country was updated successfully, id=[{}], country=[{}]", customerId, country);
                });

        return createdCustomerUser;
    }

    @Override
    public User getCustomerUserByLogin(String login) {
        return customerRepository.findCustomerUserByLogin(login)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(timeout = 1)
    @Override
    public void deleteCustomerUserByUserId(long userId) {
        long count = customerRepository.hardDeleteByUserId(userId);
        if (count == 0) {
            throw new UserNotFoundException();
        }
        userRepository.hardDeleteById(userId);
    }

    @Override
    public CustomerPublicProfile getCustomerPublicProfileByLogin(String login) {
        return customerRepository.findCustomerPublicProfileByLogin(login)
                .orElseThrow(UserNotFoundException::new);
    }
}

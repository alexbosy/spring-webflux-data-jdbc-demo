package io.cryptorush.userservice.domain.customer;

import io.cryptorush.userservice.domain.geoip.GeoIpService;
import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.domain.user.UserRepository;
import io.cryptorush.userservice.domain.user.UserValidator;
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCustomerService implements CustomerService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final GeoIpService geoIpService;
    private final CustomerRepository customerRepository;

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

    @Transactional(timeout = 1)
    @Override
    public User updateCustomerUser(String login, User user) {
        Optional<User> optionalCustomerUser = customerRepository.findCustomerUserByLogin(login);
        if (optionalCustomerUser.isPresent()) {
            User foundCustomerUser = optionalCustomerUser.get();
            user.setId(foundCustomerUser.getId());
            userValidator.validateCustomerUserCreationOrUpdate(user);
            foundCustomerUser.setName(user.getName());
            foundCustomerUser.setSurname(user.getSurname());
            foundCustomerUser.setEmail(user.getEmail());

            Customer customer = foundCustomerUser.getCustomer();
            customer.setDateOfBirth(user.getCustomer().getDateOfBirth());
            customer.setCountryOfResidence(user.getCustomer().getCountryOfResidence());
            customer.setIdentityNumber(user.getCustomer().getIdentityNumber());
            customer.setPassportNumber(user.getCustomer().getPassportNumber());

            return userRepository.save(foundCustomerUser);
        } else {
            throw new UserNotFoundException();
        }
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

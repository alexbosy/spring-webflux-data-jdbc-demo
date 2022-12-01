package io.cryptorush.userservice.domain.customer;

import org.springframework.stereotype.Service;

@Service
public class DefaultCustomerService implements CustomerService {

    private final CustomerRepository customerRepository;

    public DefaultCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}

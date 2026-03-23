package com.cts.security;


import com.cts.entity.Customer;
import com.cts.entity.InternalUser;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CustomerRepository;
import com.cts.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthContextService {

    private final InternalUserRepository internalUserRepository;
    private final CustomerRepository customerRepository;

    public InternalUser currentInternalUser(Authentication authentication) {
        return internalUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Internal user not found"));
    }

    public Customer currentCustomer(Authentication authentication) {
        return customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }
}

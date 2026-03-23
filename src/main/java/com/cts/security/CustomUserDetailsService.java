package com.cts.security;

import com.cts.entity.Customer;
import com.cts.entity.InternalUser;
import com.cts.enums.Role;
import com.cts.repository.CustomerRepository;
import com.cts.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final InternalUserRepository internalUserRepository;
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        InternalUser internalUser = internalUserRepository.findByEmail(username).orElse(null);
        if (internalUser != null) {
            return User.withUsername(internalUser.getEmail())
                    .password(internalUser.getPasswordHash())
                    .authorities(new SimpleGrantedAuthority("ROLE_" + internalUser.getRole().name()))
                    .build();
        }

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(customer.getEmail())
                .password(customer.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + Role.CUSTOMER.name()))
                .build();
    }
}
package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import java.util.Map;
import java.util.Optional;

import com.cts.dto.SignupRequest;
import com.cts.entity.Customer;
import com.cts.enums.Role;
import com.cts.exception.BusinessException;
import com.cts.repository.CustomerRepository;
import com.cts.repository.InternalUserRepository;
import com.cts.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InternalUserRepository internalUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setName("John");
        signupRequest.setEmail("john@mail.com");
        signupRequest.setPassword("pass");
        signupRequest.setAge(24);
    }

    @Test
    void customerSignup_ShouldReturnCustomerRole() {
        UserDetails userDetails = User.withUsername("john@mail.com")
                .password("encoded")
                .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                .build();

        when(customerRepository.findByEmail("john@mail.com")).thenReturn(Optional.empty());
        when(internalUserRepository.findByEmail("john@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userDetailsService.loadUserByUsername("john@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(any(), any(Map.class))).thenReturn("jwt-token");

        var response = authService.customerSignup(signupRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals(Role.CUSTOMER, response.getRole());
    }

    @Test
    void customerSignup_ShouldThrowWhenEmailExists() {
        when(customerRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(new Customer()));
        assertThrows(BusinessException.class, () -> authService.customerSignup(signupRequest));
    }
}
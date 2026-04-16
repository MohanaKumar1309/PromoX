package com.cts.service;

import java.util.List;
import java.util.Map;

import com.cts.dto.*;
import com.cts.entity.Customer;
import com.cts.entity.InternalUser;
import com.cts.enums.Role;
import com.cts.exception.BusinessException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CustomerRepository;
import com.cts.repository.InternalUserRepository;
import com.cts.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final InternalUserRepository internalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthGetDto login(LoginRequest request) {
        try {
            log.info("Login attempt for email: {}", request.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            log.info("Authentication successful for: {}", request.getEmail());
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String role = userDetails.getAuthorities().stream().findFirst()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .orElse(Role.CUSTOMER.name());
            log.info("User role: {} for email: {}", role, request.getEmail());
            
            String token = jwtService.generateToken(userDetails, Map.of("role", role));
            log.info("Token generated successfully for: {}", request.getEmail());
            
            // Get user details to include in response
            InternalUser internalUser = internalUserRepository.findByEmail(request.getEmail()).orElse(null);
            Customer customer = internalUser == null ? customerRepository.findByEmail(request.getEmail()).orElse(null) : null;
            
            Long userId = internalUser != null ? internalUser.getUserId() : (customer != null ? customer.getCustId() : null);
            String name = internalUser != null ? internalUser.getName() : (customer != null ? customer.getName() : "");
            Integer age = customer != null ? customer.getAge() : null;

            return AuthGetDto.builder()
                    .userId(userId)
                    .token(token)
                    .email(request.getEmail())
                    .name(name)
                    .role(Role.valueOf(role))
                    .age(age)
                    .build();
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    public AuthGetDto customerSignup(SignupRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()
                || internalUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setAge(request.getAge());
        Customer savedCustomer = customerRepository.save(customer);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails, Map.of("role", Role.CUSTOMER.name()));
        return AuthGetDto.builder()
                .userId(savedCustomer.getCustId())
                .token(token)
                .email(request.getEmail())
                .name(savedCustomer.getName())
                .role(Role.CUSTOMER)
                .age(savedCustomer.getAge())
                .build();
    }

    public UserGetDto createInternalUser(CreateInternalUserRequest request, Long actorUserId) {
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.CUSTOMER) {
            throw new BusinessException("Admin can create only Merchandiser, Marketing Manager, Store Manager");
        }
        if (internalUserRepository.findByEmail(request.getEmail()).isPresent()
                || customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already exists");
        }

        InternalUser user = new InternalUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        InternalUser saved = internalUserRepository.save(user);
        auditLogService.logAction(actorUserId, "USER_CREATE", "Created userId=" + saved.getUserId() + ", email=" + saved.getEmail());
        return toDto(saved);
    }

    public List<UserGetDto> getAllInternalUsers() {
        return internalUserRepository.findAll().stream().map(this::toDto).toList();
    }

    public void deleteInternalUser(Long userId, Long actorUserId) {
        InternalUser user = internalUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Internal user not found"));
        if (user.getRole() == Role.ADMIN) {
            throw new BusinessException("Admin user cannot be deleted");
        }
        if (user.getUserId().equals(actorUserId)) {
            throw new BusinessException("You cannot delete your own account");
        }

        auditLogService.logAction(actorUserId, "USER_DELETE", "Deleted userId=" + user.getUserId() + ", email=" + user.getEmail());
        internalUserRepository.delete(user);
    }

    private UserGetDto toDto(InternalUser user) {
        return UserGetDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

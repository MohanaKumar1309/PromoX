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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final InternalUserRepository internalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthGetDto login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String role = userDetails.getAuthorities().stream().findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse(Role.CUSTOMER.name());
        String token = jwtService.generateToken(userDetails, Map.of("role", role));
        return AuthGetDto.builder()
                .token(token)
                .email(request.getEmail())
                .role(Role.valueOf(role))
                .build();
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
        customerRepository.save(customer);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails, Map.of("role", Role.CUSTOMER.name()));
        return AuthGetDto.builder()
                .token(token)
                .email(request.getEmail())
                .role(Role.CUSTOMER)
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

package com.cts.controller;
import com.cts.common.ApiResponse;
import com.cts.dto.AuthGetDto;
import com.cts.dto.LoginRequest;
import com.cts.dto.SignupRequest;
import com.cts.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthGetDto>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login endpoint called with email: {}", request.getEmail());
        try {
            AuthGetDto result = authService.login(request);
            log.info("Login successful for: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.<AuthGetDto>builder()
                    .success(true)
                    .message("Login successful")
                    .data(result)
                    .build());
        } catch (Exception e) {
            log.error("Login endpoint exception for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/customer/signup")
    public ResponseEntity<ApiResponse<AuthGetDto>> customerSignup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.<AuthGetDto>builder()
                .success(true)
                .message("Customer signup successful")
                .data(authService.customerSignup(request))
                .build());
    }

}

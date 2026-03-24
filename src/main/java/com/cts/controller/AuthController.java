package com.cts.controller;
import com.cts.common.ApiResponse;
import com.cts.dto.AuthGetDto;
import com.cts.dto.LoginRequest;
import com.cts.dto.SignupRequest;
import com.cts.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthGetDto>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.<AuthGetDto>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build());
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

package com.cts.dto;


import com.cts.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
    public class CreateInternalUserRequest {
        @NotBlank
        private String name;

        @Email
        private String email;

        @NotBlank
        private String password;

        private String phone;

        @NotNull
        private Role role;
    }


package com.cts.dto;

import com.cts.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthGetDto {
    private Long userId;
    private String token;
    private String email;
    private String name;
    private Role role;
    private Integer age;
}
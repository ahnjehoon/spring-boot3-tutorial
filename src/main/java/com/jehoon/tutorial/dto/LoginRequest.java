package com.jehoon.tutorial.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
public class LoginRequest {
    @NotEmpty
    private String id;
    @NotEmpty
    private String password;
}

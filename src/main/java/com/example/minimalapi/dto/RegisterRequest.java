package com.example.minimalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration (credentials only)")
public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        @Schema(example = "jdoe")
        String username,

        @NotBlank
        @Size(min = 8, max = 128)
        @Schema(example = "a-long-secure-password")
        String password
) {
}

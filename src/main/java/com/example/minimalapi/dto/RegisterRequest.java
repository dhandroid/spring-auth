package com.example.minimalapi.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration with optional primary role enum")
public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        @Schema(example = "jdoe")
        String username,

        @NotBlank
        @Size(min = 8, max = 128)
        @Schema(example = "a-long-secure-password")
        String password,

        @Schema(
                nullable = true,
                defaultValue = "USER",
                allowableValues = {"USER", "ADMIN"},
                description = "Defaults to USER. ADMIN only if app.registration.allow-admin-role=true")
        @JsonDeserialize(using = RegistrationRoleJsonDeserializer.class)
        RegistrationRole role
) {
}

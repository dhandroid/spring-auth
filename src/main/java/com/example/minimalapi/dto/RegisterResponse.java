package com.example.minimalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Created user (no password returned)")
public record RegisterResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "jdoe")
        String username,
        @Schema(description = "Role names assigned in DB (registration adds ROLE_USER)")
        List<String> roles
) {
}

package com.example.minimalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Created user (no password returned)")
public record RegisterResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "jdoe")
        String username
) {
}

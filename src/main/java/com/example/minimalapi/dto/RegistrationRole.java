package com.example.minimalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Requested primary account role at registration. Maps to rows in the {@code roles} table.
 * JSON values: {@code USER}, {@code ADMIN} (case-insensitive). Omit or null defaults to {@code USER}.
 */
@Schema(description = "USER or ADMIN (default USER). ADMIN requires app.registration.allow-admin-role=true.")
public enum RegistrationRole {

    @Schema(description = "Maps to ROLE_USER")
    USER("ROLE_USER"),

    @Schema(description = "Maps to ROLE_ADMIN")
    ADMIN("ROLE_ADMIN");

    private final String persistedRoleName;

    RegistrationRole(String persistedRoleName) {
        this.persistedRoleName = persistedRoleName;
    }

    /** Name stored in DB / Spring Security ({@code ROLE_*}). */
    public String getPersistedRoleName() {
        return persistedRoleName;
    }
}

package com.example.minimalapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Role & authority demos", description = "JWT required; checks roles and fine-grained authorities from the database")
@SecurityRequirement(name = "bearer-jwt")
public class SecuredApiController {

    @GetMapping("/documents/read")
    @PreAuthorize("hasAuthority('DOCUMENT_READ')")
    @Operation(summary = "Read document", description = "Requires authority DOCUMENT_READ (ROLE_USER and ROLE_ADMIN)")
    public Map<String, String> readDocument() {
        return Map.of("resource", "document", "access", "read");
    }

    @GetMapping("/documents/write")
    @PreAuthorize("hasAuthority('DOCUMENT_WRITE')")
    @Operation(summary = "Write document", description = "Requires authority DOCUMENT_WRITE")
    public Map<String, String> writeDocument() {
        return Map.of("resource", "document", "access", "write");
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "Manage users", description = "Requires authority USER_MANAGE (ROLE_ADMIN only in seed data)")
    public Map<String, String> manageUsers() {
        return Map.of("resource", "users", "access", "manage");
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Admin dashboard",
            description = "Requires role ADMIN (authority ROLE_ADMIN). Grant with: INSERT INTO user_roles (user_id, role_id) VALUES (…, 2);")
    public Map<String, String> adminDashboard() {
        return Map.of("scope", "admin", "access", "dashboard");
    }

    @GetMapping("/user/home")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "User home", description = "Requires role USER (authority ROLE_USER)")
    public Map<String, String> userHome() {
        return Map.of("scope", "user", "access", "home");
    }
}

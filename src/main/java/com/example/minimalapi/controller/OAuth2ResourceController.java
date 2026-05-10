package com.example.minimalapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample REST surface for Auth0-issued access tokens.
 *
 * <p>Postman: OAuth 2.0 → Authorization Code with PKCE → use Auth0 token URL and your SPA/API client;
 * then call these endpoints with header {@code Authorization: Bearer <access_token>}.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "OAuth2 Resource API", description = "Public vs JWT-protected samples")
public class OAuth2ResourceController {

    @GetMapping("/public")
    @Operation(summary = "Public (API prefix)", description = "No Bearer token required")
    public Map<String, String> apiPublic() {
        return Map.of("access", "public", "message", "No JWT required");
    }

    @GetMapping("/private")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Private (API prefix)", description = "Requires Auth0 access token (Bearer)")
    public Map<String, String> apiPrivate(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        // JwtAuthenticationToken#getPrincipal() is the String "name" (e.g. sub), not the Jwt.
        // Use getToken() for claims; @AuthenticationPrincipal Jwt would stay null → NPE → 500.
        Jwt jwt = auth != null ? auth.getToken() : null;
        String sub = jwt != null ? jwt.getSubject() : "";
        Map<String, String> body = new HashMap<>();
        body.put("access", "private");
        body.put("subject", sub);
        return body;
    }

    /**
     * Demonstrates reading standard and custom claims after successful JWT validation.
     * The {@link Jwt} is the authentication principal for resource-server JWT login.
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Current JWT claims", description = "Returns selected claims from the validated access token")
    public Map<String, Object> me(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        Jwt jwt = auth != null ? auth.getToken() : null;
        Map<String, Object> body = new HashMap<>();
        if (jwt == null) {
            return body;
        }
        body.put("subject", jwt.getSubject());
        body.put("issuer", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null);
        body.put("audience", jwt.getAudience());
        body.put("expiresAt", jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null);
        body.put("scope", jwt.hasClaim("scope") ? jwt.getClaimAsString("scope") : jwt.getClaimAsStringList("scp"));
        if (jwt.hasClaim("permissions")) {
            body.put("permissions", jwt.getClaim("permissions"));
        }
        return body;
    }
}

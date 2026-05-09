package com.example.minimalapi.controller;

import com.example.minimalapi.dto.LoginRequest;
import com.example.minimalapi.dto.LoginResponse;
import com.example.minimalapi.dto.RegisterRequest;
import com.example.minimalapi.dto.RegisterResponse;
import com.example.minimalapi.dto.RegistrationRole;
import com.example.minimalapi.security.InMemoryJwtTokenStore;
import com.example.minimalapi.security.JwtService;
import com.example.minimalapi.security.UserSecurityService;
import com.example.minimalapi.user.AppUser;
import com.example.minimalapi.user.AppUserRepository;
import com.example.minimalapi.user.Role;
import com.example.minimalapi.user.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Registration, login, and JWT issuance")
public class AuthController {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final InMemoryJwtTokenStore tokenStore;
    private final CompromisedPasswordChecker compromisedPasswordChecker;
    private final PasswordEncoder passwordEncoder;
    private final boolean allowAdminRegistration;

    public AuthController(
            AppUserRepository userRepository,
            RoleRepository roleRepository,
            JwtService jwtService,
            InMemoryJwtTokenStore tokenStore,
            CompromisedPasswordChecker compromisedPasswordChecker,
            PasswordEncoder passwordEncoder,
            @Value("${app.registration.allow-admin-role:false}") boolean allowAdminRegistration) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
        this.passwordEncoder = passwordEncoder;
        this.allowAdminRegistration = allowAdminRegistration;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register",
            description = "Creates a user with BCrypt-hashed password. Role enum: USER (ROLE_USER) or ADMIN (ROLE_ADMIN). "
                    + "ADMIN self-registration requires app.registration.allow-admin-role=true.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "403", description = "Password compromised or ADMIN not allowed"),
                    @ApiResponse(responseCode = "409", description = "Username already taken")
            }
    )
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already taken"));
        }
        RegistrationRole requestedRole = request.role() != null ? request.role() : RegistrationRole.USER;
        if (requestedRole == RegistrationRole.ADMIN && !allowAdminRegistration) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "ADMIN registration is disabled. Set app.registration.allow-admin-role=true or grant ROLE_ADMIN in the database."));
        }
        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(request.password());
        if (decision.isCompromised()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "This password appears in known data breaches. Choose a different password."));
        }
        Role userRole = roleRepository
                .findByName(requestedRole.getPersistedRoleName())
                .orElseThrow(() -> new IllegalStateException(
                        "Role not found: " + requestedRole.getPersistedRoleName() + " — run Flyway migrations"));
        AppUser user = new AppUser();
        user.setUsername(request.username().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.getRoles().add(userRole);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already taken"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(
                        user.getId(),
                        user.getUsername(),
                        UserSecurityService.roleNames(user)));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Returns a JWT if credentials match a registered user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "JWT issued"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
        var userOpt = userRepository
                .findByUsername(request.username().trim())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
        AppUser user = userOpt.get();
        String token = jwtService.generateToken(user.getUsername());
        tokenStore.remember(token);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

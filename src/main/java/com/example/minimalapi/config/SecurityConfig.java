package com.example.minimalapi.config;

import com.example.minimalapi.security.JsonAccessDeniedHandler;
import com.example.minimalapi.security.JsonAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 <strong>Resource Server</strong> security (JWT Bearer only).
 *
 * <p>What we deliberately do <em>not</em> configure:
 * <ul>
 *   <li>{@code oauth2Login()} — that is for a <em>client</em> app doing browser login; this service only validates tokens.</li>
 *   <li>Session-based authentication — APIs stay stateless; the access token carries identity per request.</li>
 *   <li>CSRF for Bearer APIs — browsers are not posting cookies as the primary credential; disabling CSRF is normal for pure JWT APIs.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs/**",
            "/v3/api-docs/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint,
            JsonAccessDeniedHandler jsonAccessDeniedHandler)
            throws Exception {

        http
                // Stateless: no HTTP session created or used for security context.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT APIs use Authorization header, not browser cookie form posts — CSRF off is standard here.
                .csrf(csrf -> csrf.disable())

                // Consistent JSON for 401/403 from filters (Resource Server), not HTML error pages.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))

                .authorizeHttpRequests(auth -> auth
                        // Public routes: no Bearer token required.
                        .requestMatchers("/public", "/api/public").permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers("/error").permitAll()
                        // Everything else (e.g. /private, /api/private, /api/me) requires a validated JWT.
                        .anyRequest().authenticated())

                // No HTTP Basic or form login — identity comes from the Bearer access token only.
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // Registers BearerTokenAuthenticationFilter + JwtAuthenticationProvider pipeline.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }
}

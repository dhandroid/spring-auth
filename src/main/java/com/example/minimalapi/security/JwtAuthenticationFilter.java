package com.example.minimalapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import com.example.minimalapi.user.AppUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final InMemoryJwtTokenStore tokenStore;
    private final UserSecurityService userSecurityService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            InMemoryJwtTokenStore tokenStore,
            UserSecurityService userSecurityService) {
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
        this.userSecurityService = userSecurityService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            if (jwtService.isTokenWellFormedAndSigned(token) && tokenStore.isKnownValid(token)) {
                String subject = jwtService.extractSubject(token);
                userSecurityService
                        .loadUserWithRolesAndAuthorities(subject)
                        .ifPresent(user -> setAuthentication(request, user));
            }
        }
        filterChain.doFilter(request, response);
    }

    private static void setAuthentication(HttpServletRequest request, AppUser user) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        UserSecurityService.toGrantedAuthorities(user));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

package com.example.minimalapi.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryJwtTokenStore {

    private final ConcurrentHashMap<String, Long> tokenExpiryEpochMillis = new ConcurrentHashMap<>();
    private final JwtService jwtService;

    public InMemoryJwtTokenStore(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Registers a JWT issued by this server. Only tokens present here are accepted for /private.
     */
    public void remember(String token) {
        long exp = jwtService.extractExpiration(token).getTime();
        tokenExpiryEpochMillis.put(token, exp);
    }

    public boolean isKnownValid(String token) {
        Long exp = tokenExpiryEpochMillis.get(token);
        if (exp == null) {
            return false;
        }
        if (exp <= System.currentTimeMillis()) {
            tokenExpiryEpochMillis.remove(token);
            return false;
        }
        return true;
    }
}

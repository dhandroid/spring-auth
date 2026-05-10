package com.example.minimalapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Maps Auth0 JWT claims to Spring {@link GrantedAuthority} for {@code hasAuthority()},
 * {@code hasRole()}, etc.
 *
 * <p>Auth0 commonly exposes:
 * <ul>
 *   <li>{@code scope} — space-separated OAuth2 scopes from the Authorization Server</li>
 *   <li>{@code permissions} — string array when Auth0 RBAC is enabled for an API</li>
 * </ul>
 *
 * <p>Why customize: the default {@link JwtGrantedAuthoritiesConverter} only looks at
 * {@code scope}/{@code scp}. If you use RBAC permissions, you must add them explicitly.
 */
@Configuration
public class OAuth2ResourceServerJwtConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

        Converter<Jwt, Collection<GrantedAuthority>> combined =
                jwt -> {
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    Collection<GrantedAuthority> fromScopes = scopesConverter.convert(jwt);
                    if (fromScopes != null) {
                        authorities.addAll(fromScopes);
                    }
                    authorities.addAll(extractPermissions(jwt));
                    return authorities;
                };

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(combined);
        // Sets JwtAuthenticationToken#getName(); principal remains String sub — use getToken() for Jwt in controllers.
        converter.setPrincipalClaimName("sub");
        return converter;
    }

    private static Collection<GrantedAuthority> extractPermissions(Jwt jwt) {
        if (!jwt.hasClaim("permissions")) {
            return Collections.emptyList();
        }
        Object raw = jwt.getClaim("permissions");
        List<String> list = new ArrayList<>();
        if (raw instanceof Collection<?> c) {
            for (Object o : c) {
                if (o != null) {
                    list.add(o.toString());
                }
            }
        }
        List<GrantedAuthority> out = new ArrayList<>(list.size());
        for (String p : list) {
            out.add(new SimpleGrantedAuthority(p));
        }
        return out;
    }
}

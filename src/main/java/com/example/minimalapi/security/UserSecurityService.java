package com.example.minimalapi.security;

import com.example.minimalapi.user.AppUser;
import com.example.minimalapi.user.AppUserRepository;
import com.example.minimalapi.user.Authority;
import com.example.minimalapi.user.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserSecurityService {

    private final AppUserRepository userRepository;

    public UserSecurityService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> loadUserWithRolesAndAuthorities(String username) {
        return userRepository.findWithRolesAndAuthoritiesByUsername(username);
    }

    /**
     * Spring {@code hasRole("ADMIN")} checks for authority {@code ROLE_ADMIN}.
     * Fine-grained {@code hasAuthority("DOCUMENT_READ")} uses names from the {@code authorities} table.
     */
    public static List<GrantedAuthority> toGrantedAuthorities(AppUser user) {
        Set<String> names = new LinkedHashSet<>();
        for (Role role : user.getRoles()) {
            names.add(role.getName());
            for (Authority authority : role.getAuthorities()) {
                names.add(authority.getName());
            }
        }
        List<GrantedAuthority> out = new ArrayList<>(names.size());
        for (String name : names) {
            out.add(new SimpleGrantedAuthority(name));
        }
        return out;
    }

    public static List<String> roleNames(AppUser user) {
        return user.getRoles().stream().map(Role::getName).sorted().toList();
    }
}

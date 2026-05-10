package com.example.minimalapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Excludes default user/password auto-config so this app is clearly JWT-only (Resource Server),
 * not a confusing mix of generated {@code user} password and Bearer tokens.
 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class MinimalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinimalApiApplication.class, args);
    }
}

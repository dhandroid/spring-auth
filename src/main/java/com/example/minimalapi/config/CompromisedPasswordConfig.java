package com.example.minimalapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
public class CompromisedPasswordConfig {

    /**
     * When {@code app.security.compromised-password-check} is false, returns a checker that never
     * flags a password (offline / local demos). When true, delegates to Have I Been Pwned (k-anonymity API).
     */
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(
            @Value("${app.security.compromised-password-check:true}") boolean useHaveIBeenPwned) {
        if (!useHaveIBeenPwned) {
            return password -> new CompromisedPasswordDecision(false);
        }
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}

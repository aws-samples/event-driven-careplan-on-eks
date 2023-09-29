package com.amazon.careplan.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig {

    @Value("${careplan.http.api.key.token}")
    private String apiKeyFromConfig;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var filter = new ApiKeyAuthentication();
        filter.setAuthenticationManager(this::checkApiKey);
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/actuator/**")
                        .permitAll()
                )
                .addFilter(filter)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/**")
                        .authenticated()

                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf( csrf -> csrf.disable());

        return http.build();
    }

    private Authentication checkApiKey(Authentication authentication) {
        var apiKeyFromRequest = authentication.getPrincipal();
        if (!Objects.equals(apiKeyFromConfig, apiKeyFromRequest)) {
            throw new BadCredentialsException("Invalid API Key provided.");
        }
        authentication.setAuthenticated(true);
        return authentication;
    }
}

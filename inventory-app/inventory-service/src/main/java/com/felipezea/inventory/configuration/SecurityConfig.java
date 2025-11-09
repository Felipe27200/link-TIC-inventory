package com.felipezea.inventory.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the Spring Security filter chain.
     * This setup:
     *  - Disables CSRF (not needed for stateless APIs).
     *  - Allows open access to Swagger UI and authentication endpoints.
     *  - Requires authentication for all other endpoints.
     *  - Sets the session policy to stateless (no server-side sessions).
     *  - Registers a custom API key authentication filter before Spring's default auth filter.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        http
                // Disable CSRF because the API uses stateless security (no login form)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                // Define which endpoints require authentication
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no API key needed)
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/proxy/**",
                                "/actuator/**"
                        ).permitAll()
                        // Everything else requires authentication (via your custom filter)
                        .anyRequest().authenticated()
                )

                // Force stateless session: no JSESSIONID, no login sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Insert your custom API Key authentication filter
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

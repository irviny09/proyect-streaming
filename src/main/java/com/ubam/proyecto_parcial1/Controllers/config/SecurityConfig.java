package com.ubam.proyecto_parcial1.Controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import com.ubam.proyecto_parcial1.Controllers.auth.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final TokenRepository tokenRepository;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // En producción pon tu dominio real
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable) 
            .authorizeHttpRequests(req ->
                req.requestMatchers("/auth/**").permitAll()           
                    .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/img/**").permitAll()
                    .requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/user").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                    .anyRequest()
                    .authenticated()
                    
            )
            
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> 
                logout.logoutUrl("/auth/logout")
                        .addLogoutHandler((request, response , authentication) -> {
                            final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                            logout(authHeader);
                        })
                        .logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext())
                        
            );

        return http.build();
    }

    private void logout(final String token) {
    if (token == null || !token.startsWith("Bearer ")) {
        return;
    }

    final String jwtToken = token.substring(7);
    tokenRepository.findByToken(jwtToken).ifPresent(foundToken -> {
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    });
}
}

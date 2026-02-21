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
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.http.HttpHeaders;

import com.ubam.proyecto_parcial1.Controllers.auth.repository.Token;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) 
            .authorizeHttpRequests(req ->
                req.requestMatchers("/auth/**",           
                                    "/",                  
                                    "/index.html",
                                    "/registro",          
                                    "/registro.html",
                                    "/admin",             
                                    "/cliente",           
                                    "/css/**",            
                                    "/img/**",            
                                    "/js/**"              
                    ).permitAll()
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

    private void logout(final String token){
        if(token != null || !token.startsWith("Bearer ")){
            throw new IllegalArgumentException("Token invalido");
        }

        final String jwtToken = token.substring(7);
        final Token foundToken = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new IllegalArgumentException("Token invalido"));
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }
}

package com.ubam.proyecto_parcial1.Controllers.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ubam.proyecto_parcial1.Controllers.auth.repository.TokenRepository;
import com.ubam.proyecto_parcial1.Controllers.auth.service.JwtService;
import com.ubam.proyecto_parcial1.Repository.UsuarioRepository;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;


    @Override
protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    // Agregamos /js/ e /img/ para que las vistas carguen sus recursos sin problemas
    return path.equals("/") || 
           path.startsWith("/auth/") || 
           path.startsWith("/css/") || 
           path.startsWith("/js/") || 
           path.startsWith("/img/");
}

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwtToken = null;
        String userEmail = null;

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }else if (request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if("access_token".equals(cookie.getName())){
                    jwtToken = cookie.getValue();
                }
            }
        }

        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            // Si el token está mal formado o hay error al extraer, seguimos
            filterChain.doFilter(request, response);
            return;
        }

        // Validar si tenemos email y el usuario no está ya autenticado
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Verificar en la base de datos si el token no ha sido revocado o ha expirado
            var isTokenValidInDb = tokenRepository.findByToken(jwtToken)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            var usuario = usuarioRepository.findByEmail(userEmail).orElse(null);
            if (usuario != null && jwtService.isTokenValid(jwtToken, usuario) && isTokenValidInDb) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() 
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

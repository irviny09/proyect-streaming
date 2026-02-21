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
        // Rutas que el filtro ignorará por completo
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

        // 1. Eliminar validaciones manuales de path aquí si ya usas shouldNotFilter
        
        String jwtToken = null;
        String userEmail = null;

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Extracción de Token (Header o Cookie)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        } else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                }
            }
        }

        // Si no hay token, simplemente seguimos a la siguiente cadena de filtros
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtService.extractUsername(jwtToken);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                // 3. Validación crucial: Token no revocado en BD y estado del usuario
                var isTokenValidInDb = tokenRepository.findByToken(jwtToken)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                var usuario = usuarioRepository.findByEmail(userEmail).orElse(null);

                if (usuario != null && usuario.isVerificado() && 
                    jwtService.isTokenValid(jwtToken, usuario) && isTokenValidInDb) {
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() 
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Loguear el error si es necesario
        }

        filterChain.doFilter(request, response);
    }
}

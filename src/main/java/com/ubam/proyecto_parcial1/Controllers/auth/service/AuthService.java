package com.ubam.proyecto_parcial1.Controllers.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ubam.proyecto_parcial1.Controllers.auth.controller.LoginRequest;
import com.ubam.proyecto_parcial1.Controllers.auth.controller.RegisterRequest;
import com.ubam.proyecto_parcial1.Controllers.auth.controller.TokenResponse;
import com.ubam.proyecto_parcial1.Controllers.auth.repository.Token;
import com.ubam.proyecto_parcial1.Controllers.auth.repository.TokenRepository;
import com.ubam.proyecto_parcial1.Models.Usuario;
import com.ubam.proyecto_parcial1.Repository.RolRepository;
import com.ubam.proyecto_parcial1.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository; 
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    

    public TokenResponse register(RegisterRequest request) {
        // 1. Verificar si el usuario ya existe antes de llamar al SP
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Llamar al procedimiento almacenado
        rolRepository.findByNombre(request.role()).ifPresent(rol -> {
            usuarioRepository.addNewUser(
                request.name(),
                request.apellidoPat(),
                request.apellidoMat(),
                request.email(),
                passwordEncoder.encode(request.password()),
                rol.getId(),
                true
            );
        });

        // 3. Recuperar al usuario recién creado
        var user = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Error al recuperar el usuario creado"));

        // 4. Generar y guardar tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request) {
        // Autenticación contra MariaDB
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return new TokenResponse(jwtToken, refreshToken);
    }
    public TokenResponse refreshToken(String authHeader) {
        // Lógica de refresh aquí
        return null;
    }

    private void saveUserToken(Usuario usuario , String jwtToken){
        Token token = Token.builder()
                .usuario(usuario)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    private void revokeAllUserTokens(Usuario user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}

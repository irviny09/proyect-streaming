package com.ubam.proyecto_parcial1.Controllers.auth.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ubam.proyecto_parcial1.Controllers.auth.controller.LoginRequest;
import com.ubam.proyecto_parcial1.Controllers.auth.controller.RegisterRequest;
import com.ubam.proyecto_parcial1.Controllers.auth.controller.TokenResponse;
import com.ubam.proyecto_parcial1.Controllers.auth.repository.Token;
import com.ubam.proyecto_parcial1.Controllers.auth.repository.TokenRepository;
import com.ubam.proyecto_parcial1.Models.Rol;
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

    @Autowired
    private JavaMailSender mailSender;
    

    public TokenResponse register(RegisterRequest request) {
        String token = UUID.randomUUID().toString(); // Generamos el token

        Rol rol = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado"));
        // Guardamos en la BD usando tu procedimiento
        usuarioRepository.addNewUser(
            request.name(), request.apellidoPat(), request.apellidoMat(),
            request.email(), passwordEncoder.encode(request.password()),
            rol.getId(), true, token 
        );

        // Llamamos al método (esto quita el error rojo)
        enviarEmailVerificacion(request.email(), token);

        return new TokenResponse("PENDIENTE_VERIFICACION", null);
    }

    private void enviarEmailVerificacion(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verifica tu cuenta");
        message.setText("Haz clic aquí: http://localhost:7890/auth/verify?token=" + token);
        mailSender.send(message);
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        var user = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse refreshToken(final String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new IllegalArgumentException("Token invalido");
        }


        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if(userEmail == null){
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        final Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if(!jwtService.isTokenValid(refreshToken, usuario)){
            throw new IllegalArgumentException("Invalid Refresh Token");
        }

        final String accesToken = jwtService.generateToken(usuario);
        revokeAllUserTokens(usuario);
        saveUserToken(usuario, accesToken);
        return new TokenResponse(accesToken, refreshToken);
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

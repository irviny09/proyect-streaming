package com.ubam.proyecto_parcial1.Controllers.auth.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.ubam.proyecto_parcial1.Models.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService {
    
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshExpiration;

    public String generateToken(final Usuario usuario){
        return buildToken(usuario , jwtExpiration);
    }
    public String generateRefreshToken(final Usuario usuario){
        return buildToken(usuario , refreshExpiration);
    }

    private String buildToken(final Usuario usuario, final long expiration){
        return Jwts.builder()
                .id(usuario.getId().toString())
                .claim("role", usuario.getRol().getNombre())
                .claim("name" , usuario.getNombre())
                .subject(usuario.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

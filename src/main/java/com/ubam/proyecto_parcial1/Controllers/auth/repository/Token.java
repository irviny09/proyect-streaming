package com.ubam.proyecto_parcial1.Controllers.auth.repository;
import com.ubam.proyecto_parcial1.Models.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_ope_tokens")
public class Token {
    
    public enum TokenType {
        BEARER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenId") 
    private Integer id;

    @Column(name = "Token_Token", unique = true, nullable = false) 
    private String token;

    @Column(name = "Token_Revoked") 
    private boolean revoked;

    @Column(name = "Token_Expired") 
    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Token_UsuarioId") 
    private Usuario usuario;
}


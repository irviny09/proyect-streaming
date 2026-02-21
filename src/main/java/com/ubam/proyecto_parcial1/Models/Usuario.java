package com.ubam.proyecto_parcial1.Models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ubam.proyecto_parcial1.Controllers.auth.repository.Token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "tbl_ope_usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuarioId")
    private Integer id;

    @Column(name = "Usuario_Nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "Usuario_ApellidoPat", nullable = false, length = 45)
    private String apellidoPaterno;

    @Column(name = "Usuario_ApellidoMat", nullable = false, length = 45)
    private String apellidoMaterno;

    @Column(name = "Usuario_Email", nullable = false, length = 80, unique = true)
    private String email;

    @Column(name = "Usuario_Password", nullable = false, length = 255) // BCrypt genera cadenas largas
    private String password;

    @ManyToOne
    @JoinColumn(name = "Usuario_RolId", nullable = false)
    private Rol rol;

    @Column(name = "Usuario_Activo", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "usuario")
    private List<Token> tokens;

    @Column(name = "Usuario_TokenVerificacion", length = 100)
    private String usuarioTokenVerificacion;

    @Column(name = "verificado")
    private boolean verificado;

    // --- MÉTODOS OBLIGATORIOS DE USERDETAILS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
      
        return activo;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }
    public String getUsuarioTokenVerificacion() {
        return usuarioTokenVerificacion;
    }

    public void setUsuarioTokenVerificacion(String usuarioTokenVerificacion) {
        this.usuarioTokenVerificacion = usuarioTokenVerificacion;
    }
}
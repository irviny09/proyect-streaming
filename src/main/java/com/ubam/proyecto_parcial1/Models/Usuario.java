package com.ubam.proyecto_parcial1.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_ope_usuarios")
public class Usuario {
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

    @Column(name = "Usuario_Email", nullable = false, length = 80)
    private String email;

    @Column(name = "Usuario_Password", nullable = false, length = 45)
    private String password;

    @ManyToOne
    @JoinColumn(name = "Usuario_RolId", nullable = false)
    private Rol rol;

    @Column(name = "Usuario_Activo", nullable = false)
    private boolean activo;

    

    public Usuario() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    

    // Getters y Setters
}

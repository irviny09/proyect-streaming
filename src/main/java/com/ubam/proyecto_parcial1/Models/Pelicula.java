package com.ubam.proyecto_parcial1.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_ope_peliculas")
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PeliculaId")
    private Integer id;

    @Column(name = "Pelicula_Nombre", nullable = false, length = 45)
    private String nombre;

    
    @Lob
    @Column(name = "Pelicula_Descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "Pelicula_Activo", nullable = false)
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "Pelicula_ImagenId", nullable = false)
    private Imagen imagen;

    @ManyToOne
    @JoinColumn(name = "Pelicula_GeneroId", nullable = false)
    private Genero genero;

    

    public Pelicula() {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

}

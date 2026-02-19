package com.ubam.proyecto_parcial1.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_ope_imagenes")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImagenId")
    private Integer imagenId;

    @Column(name = "Imagen_Nombre", nullable = false, length = 45)
    private String imageneNombre;

    @Column(name = "Imagen_URL", nullable = false, length = 200)
    private String imageUrl;

    

    public Imagen() {
    }

    public Integer getImagenId() {
        return imagenId;
    }

    public void setImagenId(Integer imagenId) {
        this.imagenId = imagenId;
    }

    public String getImageneNombre() {
        return imageneNombre;
    }

    public void setImageneNombre(String imageneNombre) {
        this.imageneNombre = imageneNombre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    
    
    }


package com.ubam.proyecto_parcial1.Controllers.auth.controller;

public record RegisterRequest(
    String email,
    String password,
    String name,
    String apellidoPat,
    String apellidoMat,
    String role
) {
}
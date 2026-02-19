package com.ubam.proyecto_parcial1.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.ubam.proyecto_parcial1.Models.Usuario;

import jakarta.transaction.Transactional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Transactional
    @Procedure(procedureName = "sp_addNewUser")
    void addNewUser(
        @Param("_nombre") String nombre,
        @Param("_apellidoP") String apellidoP,
        @Param("_apellidoM") String apellidoM,
        @Param("_email") String email,
        @Param("_password") String password,
        @Param("_rolId") Integer rolId,
        @Param("_activo") Boolean activo
    );

    @Query(value = "CALL sp_showAllUser()" , nativeQuery = true)
    List<Map<String, Object>> showAllUsers();

    @Transactional
    @Procedure(procedureName = "sp_updateActiveUserById")
    void updateActiveUserById(
        @Param("_usuarioId") Integer usuarioId,
        @Param("_activo") Boolean activo
    );

    @Transactional
    @Procedure(procedureName = "sp_deleteUserById")
    void deleteUserById(@Param("_usuarioId") Integer usuarioId);

    Optional<Usuario> findByEmail(String email);
}

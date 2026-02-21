package com.ubam.proyecto_parcial1.Repository;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.ubam.proyecto_parcial1.Models.Pelicula;

import jakarta.transaction.Transactional;

public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

    @Transactional
    @Procedure(procedureName = "sp_addNewMovie")
    void addNewMovie(
        @Param("_nombre") String nombre,
        @Param("_descripcion") String descripcion,
        @Param("_activo") Boolean activo,
        @Param("_genero") Integer generoId,
        @Param("url") String url,
        @Param("_trailerURL") String trailerURL
    );

    @Query(value = "call sp_showAllMovie()" , nativeQuery = true)
    List<Map<String, Object>> showAllMovies();

    @Transactional
    @Procedure(procedureName = "sp_updateActiveById")
    void updateActiveById(
        @Param("_peliculaId") Integer peliculaId,
        @Param("_activo") Boolean activo
    );
    
    @Query(value = "call sp_showMovieById(:peliculaId)" , nativeQuery = true)
    List<Map<String, Object>> showMovieById(@Param("_peliculaId") Integer peliculaId);
    
}

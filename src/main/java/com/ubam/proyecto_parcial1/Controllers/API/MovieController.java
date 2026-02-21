package com.ubam.proyecto_parcial1.Controllers.API;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ubam.proyecto_parcial1.Repository.PeliculaRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/operacional")
@CrossOrigin("*")
public class MovieController {
    
    @Autowired
    private PeliculaRepository peliculaRepository;

    @PostMapping("/registrar")
    public String registrarPelicula(
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("activo") Boolean activo,
        @RequestParam("generoId") Integer generoId,
        @RequestParam("archivo") MultipartFile archivo
    ){
        try{
            String rutaCarpeta = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
            File carpeta = new File(rutaCarpeta);
            if (!carpeta.exists()) carpeta.mkdirs();

            String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path rutaCompleta = Paths.get(rutaCarpeta + nombreArchivo);
            
            Files.write(rutaCompleta, archivo.getBytes());

            String urlImagen = "/uploads/" + nombreArchivo;

            peliculaRepository.addNewMovie(nombre, descripcion, activo, generoId, urlImagen);
            return "Pelicula registrada exitosamente";
        } catch (Exception e) {
            return "Error al registrar la pelicula: " + e.getMessage();
        }
    }
    

    @GetMapping("/mostrar")
    public List<Map<String, Object>> mostrarPeliculas() {
        return peliculaRepository.showAllMovies();
    }
    
    @PostMapping("/actualizarMovie")
    public String actualizarPelicula(
        @RequestBody Map<String , Object> payload)
    {
        try {
            Integer peliculaId = Integer.parseInt(payload.get("peliculaId").toString());
            Boolean activo = Boolean.parseBoolean(payload.get("activo").toString());

            peliculaRepository.updateActiveById(peliculaId, activo);
            return "Pelicula actualizada exitosamente";
        } catch (Exception e) {
            return "Error al actualizar la pelicula: " + e.getMessage();
        }
    }
}

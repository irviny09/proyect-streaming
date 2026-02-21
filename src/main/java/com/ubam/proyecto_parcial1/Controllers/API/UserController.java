package com.ubam.proyecto_parcial1.Controllers.API;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubam.proyecto_parcial1.Models.Usuario;
import com.ubam.proyecto_parcial1.Repository.UsuarioRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/operacional")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/registrarCliente")
    public String registrarCliente(
        @RequestParam("nombre") String nombre,
        @RequestParam("apellidoP") String apellidoP,
        @RequestParam("apellidoM") String apellidoM,
        @RequestParam("email") String email,
        @RequestParam("password") String password
    ){
        try{            
            usuarioRepository.addNewUser(nombre, apellidoP, apellidoM, email, password, 2 , true, null);
            return "Usuario registrado exitosamente";
        } catch (Exception e) {
            return "Error al registrar el usuario: " + e.getMessage();
        }
    }

    @GetMapping("/mostrarClientes")
    public List<Map<String, Object>> mostrarClientes() {
        return usuarioRepository.showAllUsers();
    }

    @PostMapping("/actualizarCliente")
    public String actualizarCliente(
        @RequestBody Map<String , Object> payload)
    {
        try {
            Integer clienteId = Integer.parseInt(payload.get("clienteId").toString());
            Boolean activo = Boolean.parseBoolean(payload.get("activo").toString());

            usuarioRepository.updateActiveUserById(clienteId, activo);
            return "Cliente actualizado exitosamente";
        } catch (Exception e) {
            return "Error al actualizar el cliente: " + e.getMessage();
        }
    }
    
    
    
}

package com.ubam.proyecto_parcial1.Controllers.View;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/user")
    public String user() {
        return "cliente";
    }

    @GetMapping("/registrar")
    public String register() {
        return "registro";
    }
    
    
    
    
    
}

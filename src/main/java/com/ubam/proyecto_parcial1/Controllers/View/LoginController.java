package com.ubam.proyecto_parcial1.Controllers.View;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/index";
    }
    
    
}

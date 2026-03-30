package Videoclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/catalogo", "/perfil", "/login", "/panel", "/contacto"})
    public String spa() {
        return "forward:/index.html";
    }
}

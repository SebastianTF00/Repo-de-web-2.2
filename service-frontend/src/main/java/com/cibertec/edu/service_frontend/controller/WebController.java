package com.cibertec.edu.service_frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String index() {
        // Esto buscará index.html en tu carpeta templates
        return "index";
    }
}
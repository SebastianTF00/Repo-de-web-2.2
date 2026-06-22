package com.cibertec.edu.service_frontend.controller;

import com.cibertec.edu.service_frontend.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Controller
public class ProductoWebController {

    @Autowired
    private RestTemplate restTemplate;

    private final String GATEWAY_URL = "http://localhost:8080/api/productos";

    @GetMapping("/")
    public String listarProductos(Model model) {
        Producto[] productosArray = restTemplate.getForObject(GATEWAY_URL, Producto[].class);
        List<Producto> productos = Arrays.asList(productosArray != null ? productosArray : new Producto[0]);
        
        model.addAttribute("productos", productos);
        return "inicio";
    }
}
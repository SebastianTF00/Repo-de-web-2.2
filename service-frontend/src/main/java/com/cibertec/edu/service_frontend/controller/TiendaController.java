package com.cibertec.edu.service_frontend.controller;

import com.cibertec.edu.service_frontend.model.Producto;
import com.cibertec.edu.service_frontend.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class TiendaController {
    @Autowired
    private RestTemplate restTemplate;
    private final String PRODUCTOS_URL = "http://service-gateway/api/productos";
    private final String CATEGORIAS_URL = "http://service-gateway/api/categorias";

    @GetMapping("/tienda")
    public String mostrarTienda(Model model) {
        cargarDatos(model);
        return "tienda";
    }

    @GetMapping("/tienda-usuario")
    public String mostrarTiendaUsuario(Model model) {
        cargarDatos(model);
        return "tienda-usuario";
    }

    private void cargarDatos(Model model) {Producto[] productosArray = restTemplate.getForObject(PRODUCTOS_URL, Producto[].class);

        Categoria[] categoriasArray = restTemplate.getForObject(CATEGORIAS_URL, Categoria[].class);
        List<Producto> productos = Arrays.asList(productosArray != null ? productosArray : new Producto[0]);
        List<Categoria> categorias = Arrays.asList(categoriasArray != null ? categoriasArray : new Categoria[0]);
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
    }
}
package com.cibertec.edu.service_frontend.controller;

import com.cibertec.edu.service_frontend.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
public class ProductoWebController {

    @Autowired
    private RestTemplate restTemplate;

    // Todas las URLs apuntan al nombre del servicio en Eureka, NO a localhost
    private final String GATEWAY_URL = "http://service-gateway/api/productos";

    @GetMapping("/")
    public String listarProductos(Model model) {
        Producto[] productosArray = restTemplate.getForObject(GATEWAY_URL, Producto[].class);
        List<Producto> productos = Arrays.asList(productosArray != null ? productosArray : new Producto[0]);
        model.addAttribute("productos", productos);
        return "index";
    }

    @GetMapping("/carrito")
    public String mostrarCarrito() {
        return "carrito";
    }

    @GetMapping("/tienda/categorias")
    public String vistaCategoriasCliente(Model model) {
        try {
            // CORREGIDO: URL dinámica
            Object[] categoriasArray = restTemplate.getForObject("http://service-gateway/api/categorias", Object[].class);
            List<Object> categorias = Arrays.asList(categoriasArray != null ? categoriasArray : new Object[0]);
            model.addAttribute("categorias", categorias);
        } catch (Exception e) {
            model.addAttribute("categorias", new ArrayList<>());
        }
        return "tienda-categorias";
    }

    @GetMapping("/api/public/productos/categoria/{id}")
    @ResponseBody
    public List<Object> obtenerProductosPorCategoriaAsync(@PathVariable("id") Integer id) {
        try {
            // CORREGIDO: URL dinámica
            Object[] productosArray = restTemplate.getForObject("http://service-gateway/api/productos/categoria/" + id, Object[].class);
            return Arrays.asList(productosArray != null ? productosArray : new Object[0]);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/configurador")
    public String mostrarConfigurador(Model model) {
        try {
            // CORREGIDO: Todas las llamadas a las categorías específicas
            Object[] procesadores = restTemplate.getForObject("http://service-gateway/api/productos/categoria/1", Object[].class);
            model.addAttribute("procesadores", Arrays.asList(procesadores != null ? procesadores : new Object[0]));

            Object[] placas = restTemplate.getForObject("http://service-gateway/api/productos/categoria/3", Object[].class);
            model.addAttribute("placas", Arrays.asList(placas != null ? placas : new Object[0]));

            Object[] gpus = restTemplate.getForObject("http://service-gateway/api/productos/categoria/2", Object[].class);
            model.addAttribute("gpus", Arrays.asList(gpus != null ? gpus : new Object[0]));

        } catch (Exception e) {
            model.addAttribute("procesadores", new ArrayList<>());
            model.addAttribute("placas", new ArrayList<>());
            model.addAttribute("gpus", new ArrayList<>());
        }
        return "configurador";
    }
}
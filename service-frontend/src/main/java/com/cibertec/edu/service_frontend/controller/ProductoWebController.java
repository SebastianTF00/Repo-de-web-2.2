package com.cibertec.edu.service_frontend.controller;

import com.cibertec.edu.service_frontend.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.*;

@Controller
public class ProductoWebController {

    @Autowired
    private RestTemplate restTemplate;

    private final String GATEWAY_URL = "http://localhost:8080/api/productos";

    // 1. Catálogo principal de la tienda (Inicio)
    @GetMapping("/")
    public String listarProductos(Model model) {
        Producto[] productosArray = restTemplate.getForObject(GATEWAY_URL, Producto[].class);
        List<Producto> productos = Arrays.asList(productosArray != null ? productosArray : new Producto[0]);

        model.addAttribute("productos", productos);
        return "inicio";
    }

    // 2. Carrito de compras público
    @GetMapping("/carrito")
    public String mostrarCarrito() {
        return "carrito";
    }

    // 3. Nueva vista interactiva del cliente (La que arreglamos con el Sidebar y JS)
    @GetMapping("/tienda/categorias")
    public String vistaCategoriasCliente(Model model) {
        try {
            Object[] categoriasArray = restTemplate.getForObject("http://localhost:8080/api/categorias", Object[].class);
            List<Object> categorias = Arrays.asList(categoriasArray != null ? categoriasArray : new Object[0]);

            model.addAttribute("categorias", categorias);
        } catch (Exception e) {
            System.out.println("=== ERROR AL CARGAR CATEGORÍAS PÚBLICAS ===");
            e.printStackTrace();
            model.addAttribute("categorias", new ArrayList<>());
        }
        return "tienda-categorias";
    }

    // 4. API Asíncrona para que funcione el "Ver Componentes" y los Filtros sin recargar página
    @GetMapping("/api/public/productos/categoria/{id}")
    @ResponseBody
    public List<Object> obtenerProductosPorCategoriaAsync(@PathVariable("id") Integer id) {
        try {
            Object[] productosArray = restTemplate.getForObject("http://localhost:8080/api/productos/categoria/" + id, Object[].class);
            return Arrays.asList(productosArray != null ? productosArray : new Object[0]);
        } catch (Exception e) {
            System.out.println("=== ERROR EN FETCH ASÍNCRONO DE PRODUCTOS ===");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
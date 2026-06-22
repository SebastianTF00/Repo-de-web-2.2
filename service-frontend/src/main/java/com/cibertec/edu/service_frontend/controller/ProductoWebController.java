package com.cibertec.edu.service_frontend.controller;

import com.cibertec.edu.service_frontend.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;
import java.util.*;

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
    @GetMapping("/inventario")
    public String mostrarInventario(Model model) {
        // 1. El Frontend le pide la lista completa de productos al GATEWAY
        Producto[] productosArray = restTemplate.getForObject("http://localhost:8080/api/productos", Producto[].class);
        List<Producto> productos = Arrays.asList(productosArray != null ? productosArray : new Producto[0]);

        // 2. Le inyecta los datos a la plantilla "inventario.html"
        model.addAttribute("productos", productos);
        return "inventario";
    }
    @GetMapping("/carrito")
    public String mostrarCarrito() {
        return "carrito";
    }
    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        // El frontend le pide las categorías al API Gateway (Puerto 8080)
        // Nota: Revisa si tu microservicio de productos expone las categorías en "/api/categorias" o directo en "/categorias"
        try {
            Object[] categoriasArray = restTemplate.getForObject("http://localhost:8080/api/categorias", Object[].class);
            List<Object> categorias = Arrays.asList(categoriasArray != null ? categoriasArray : new Object[0]);
            model.addAttribute("categorias", categorias);
        } catch (Exception e) {
            // Por si acaso el microservicio de categorías aún no tiene datos o rutas listas
            model.addAttribute("categorias", new ArrayList<>());
        }

        return "categorias"; // Retorna el archivo categorias.html
    }
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        Object[] usuariosArray = restTemplate.getForObject("http://localhost:8080/api/usuarios", Object[].class);
        List<Object> usuarios = Arrays.asList(usuariosArray != null ? usuariosArray : new Object[0]);
        model.addAttribute("usuarios", usuarios);
        return "usuarios"; //usuarios.html
    }

    @GetMapping("/pedidos")
    public String listarPedidos(Model model) {
        Object[] pedidosArray = restTemplate.getForObject("http://localhost:8080/api/pedidos", Object[].class);
        List<Object> pedidos = Arrays.asList(pedidosArray != null ? pedidosArray : new Object[0]);
        model.addAttribute("pedidos", pedidos);
        return "pedidos"; //pedidos.html
    }
    // ==========================================
//   FORMULARIOS DE CREACIÓN (MÉTODO GET)
// ==========================================

    // 1. Abrir Formulario para Nuevo Producto (Ya corregido con sus categorías)
    @GetMapping("/producto/nuevo")
    public String formularioProducto(Model model) {
        model.addAttribute("producto", new Producto()); // Reemplaza 'Producto' por tu clase Entidad exacta si varía
        try {
            Object[] categoriasArray = restTemplate.getForObject("http://localhost:8080/api/categorias", Object[].class);
            List<Object> categorias = Arrays.asList(categoriasArray != null ? categoriasArray : new Object[0]);
            model.addAttribute("categorias", categorias);
        } catch (Exception e) {
            model.addAttribute("categorias", new ArrayList<>());
        }
        return "nuevo-producto";
    }

    // 2. Abrir Formulario para Nuevo Pedido
    @GetMapping("/pedidos/nuevo")
    public String formularioPedido(Model model) {
        model.addAttribute("pedido", new Object()); // Cambia 'new Object()' por 'new Pedido()' cuando tengas la clase importada

        // Si tu formulario-pedido necesita listar los usuarios o productos para seleccionarlos, los traemos del Gateway:
        try {
            Object[] usuariosArray = restTemplate.getForObject("http://localhost:8080/api/usuarios", Object[].class);
            model.addAttribute("usuarios", Arrays.asList(usuariosArray != null ? usuariosArray : new Object[0]));
        } catch (Exception e) {
            model.addAttribute("usuarios", new ArrayList<>());
        }
        return "formulario-pedido";
    }

    // 3. Abrir Formulario para Nuevo Usuario
    @GetMapping("/usuarios/nuevo")
    public String formularioUsuario(Model model) {
        model.addAttribute("usuario", new Object()); // Cambia 'new Object()' por 'new Usuario()' cuando tengas la clase importada
        return "formulario-usuario";
    }

    // 4. Abrir Formulario para Nueva Categoría
    @GetMapping("/categorias/nueva")
    public String formularioCategoria(Model model) {
        model.addAttribute("categoria", new Object()); // Cambia 'new Object()' por 'new Categoria()' cuando tengas la clase importada
        return "formulario-categoria";
    }

    @GetMapping("/producto/editar/{id}")
    public String formularioEditarProducto(@PathVariable("id") Integer id, Model model) {
        try {
            // Obligamos a RestTemplate a transformar el JSON directamente en tu clase Producto real
            Producto producto = restTemplate.getForObject("http://localhost:8080/api/productos/" + id, Producto.class);
            model.addAttribute("producto", producto);

            // Traemos las categorías para el select del formulario
            Object[] categoriasArray = restTemplate.getForObject("http://localhost:8080/api/categorias", Object[].class);
            model.addAttribute("categorias", Arrays.asList(categoriasArray != null ? categoriasArray : new Object[0]));

        } catch (Exception e) {
            System.out.println("=== ERROR CRÍTICO EN EDITAR PRODUCTO ===");
            e.printStackTrace();

            // Respaldo por si falla la conexión al Gateway, para que Thymeleaf no tire un 500
            model.addAttribute("producto", new Producto());
            model.addAttribute("categorias", new ArrayList<>());
        }

        return "editar-producto";
    }
    @PostMapping("/producto/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto) {
        try {
            // Le enviamos el objeto modificado al API Gateway mediante un POST de RestTemplate
            // Nota: Si tu microservicio diferencia la edición usando un método PUT, puedes cambiarlo por:
            // restTemplate.put("http://localhost:8080/api/productos/" + producto.getIdProducto(), producto);

            restTemplate.postForObject("http://localhost:8080/api/productos", producto, Object.class);

        } catch (Exception e) {
            System.out.println("=== ERROR AL GUARDAR EL PRODUCTO VIA GATEWAY ===");
            e.printStackTrace();
        }

        // Una vez guardado con éxito o manejado el error, te regresa directamente a la tabla del inventario
        return "redirect:/inventario";
    }
    @GetMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        try {
            // Le enviamos la petición de borrado al API Gateway usando el método DELETE de RestTemplate
            // Nota: Si tu Gateway no lleva el prefijo, quítaselo aquí abajo: "http://localhost:8080/productos/"
            restTemplate.delete("http://localhost:8080/api/productos/" + id);

        } catch (Exception e) {
            System.out.println("=== ERROR AL ELIMINAR EL PRODUCTO VIA GATEWAY ===");
            e.printStackTrace();
        }

        // Al terminar de procesar, refresca el panel de inventario automáticamente
        return "redirect:/inventario";
    }
}
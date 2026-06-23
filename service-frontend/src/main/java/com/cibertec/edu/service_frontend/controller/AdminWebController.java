package com.cibertec.edu.service_frontend.controller;

import com.cibertec.edu.service_frontend.model.Categoria;
import com.cibertec.edu.service_frontend.model.Producto;
import com.cibertec.edu.service_frontend.model.Usuario;
import com.cibertec.edu.service_frontend.model.Pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class AdminWebController {


    // ==========================================
    // 1. LOGIN
    // ==========================================
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // ¡NUEVO! Recibe el formulario de login y te deja pasar
    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo, 
                                @RequestParam("contrasenia") String contrasenia, 
                                Model model) {
        try {
            // Empaquetamos los datos del formulario
            Map<String, String> credenciales = new HashMap<>();
            credenciales.put("correo", correo);
            credenciales.put("contrasenia", contrasenia);

            // Enviamos al backend para que valide con BCrypt
            restTemplate.postForEntity(USUARIOS_URL + "/login", credenciales, Usuario.class);
            
            // Si no hay error, el login fue un éxito
            return "redirect:/inventario";
            
        } catch (HttpClientErrorException e) {
            // Si el backend rechaza las credenciales
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "login";
        } catch (Exception e) {
            // Si el servidor está apagado
            model.addAttribute("error", "Error de conexión con el servidor.");
            return "login";
        }
    }

    // ==========================================
    // 2. INVENTARIO (PRODUCTOS)
    // ==========================================
    @Autowired
    private RestTemplate restTemplate;

    private final String PRODUCTOS_URL = "http://localhost:8080/api/productos";
    private final String CATEGORIAS_URL = "http://localhost:8080/api/categorias";
    private final String USUARIOS_URL = "http://localhost:8080/api/usuarios";
    private final String PEDIDOS_URL = "http://localhost:8080/api/pedidos";
    
    // --- LISTAR BLINDADO CONTRA VALORES NULOS ---
    @GetMapping("/inventario")
    public String mostrarInventario(Model model) {
        try {
            Producto[] response = restTemplate.getForObject(PRODUCTOS_URL, Producto[].class);
            List<Producto> productos = Arrays.asList(response != null ? response : new Producto[0]);
            
            model.addAttribute("productos", productos);
            model.addAttribute("totalProductos", productos.size());
            
            // Protección contra NullPointerException si algún producto tiene stock nulo
            long bajoStockCount = productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() < 5)
                .count();
                
            model.addAttribute("bajoStock", bajoStockCount);
        } catch (Exception e) {
            model.addAttribute("productos", new ArrayList<>());
            model.addAttribute("totalProductos", 0);
            model.addAttribute("bajoStock", 0);
            System.out.println("ERROR AL LISTAR: " + e.getMessage());
        }
        return "inventario";
    }

    // --- GUARDAR (NUEVO O EDITAR) ---
    @PostMapping("/producto/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        // ESTO NOS DIRÁ SI LOS DATOS LLEGAN O NO
        System.out.println("DEBUG: Guardando producto: " + producto.getNombre() + " con ID: " + producto.getIdProducto());
        
        if (producto.getIdProducto() == null) {
            restTemplate.postForObject(PRODUCTOS_URL, producto, Producto.class);
        } else {
            restTemplate.put(PRODUCTOS_URL + "/" + producto.getIdProducto(), producto);
        }
        return "redirect:/inventario";
    }

    // --- ELIMINAR CONTROLADO ---
    @GetMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        try {
            restTemplate.delete(PRODUCTOS_URL + "/" + id);
            System.out.println("PRODUCTO " + id + " ELIMINADO CON ÉXITO.");
        } catch (Exception e) {
            System.out.println("NO SE PUDO ELIMINAR EL PRODUCTO ID " + id);
            System.out.println("MOTIVO EXACTO DEL SERVIDOR: " + e.getMessage()); 
        }
        return "redirect:/inventario";
    }

    // --- DEMAS ENLACES ---
    @GetMapping("/producto/nuevo")
    public String nuevoProducto(Model model) {
        // 1. Creamos el objeto vacío
        Producto p = new Producto();
        p.setCategoria(new Categoria());
        
        // 2. LLAMAMOS A LA API PARA TRAER LAS CATEGORÍAS REALES
        // Esto va justo aquí, antes de enviar el modelo al HTML
        try {
            Categoria[] cats = restTemplate.getForObject("http://localhost:8080/api/categorias", Categoria[].class);
            model.addAttribute("categorias", cats != null ? Arrays.asList(cats) : new ArrayList<>());
        } catch (Exception e) {
            // Si la API falla, enviamos una lista vacía para que no se caiga la página
            model.addAttribute("categorias", new ArrayList<>());
        }

        // 3. Enviamos el objeto al modelo
        model.addAttribute("producto", p);
        
        return "producto-form";
    }

    @GetMapping("/producto/editar/{id}")
    public String editarProducto(@PathVariable("id") Integer id, Model model) {
        // 1. Llamamos a la API para obtener el producto específico
        Producto producto = restTemplate.getForObject(PRODUCTOS_URL + "/" + id, Producto.class);
        
        // 2. Traemos las categorías para el desplegable (fundamental para editar la categoría)
        Categoria[] cats = restTemplate.getForObject("http://localhost:8080/api/categorias", Categoria[].class);
        
        // 3. Enviamos ambos objetos al modelo para que el HTML los llene
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", cats != null ? Arrays.asList(cats) : new ArrayList<>());
        
        return "editar-producto"; // Asegúrate de que el nombre del archivo sea este
    }



    // ==========================================
    // 3. CATEGORÍAS
    // ==========================================
    @GetMapping("/categorias")
    public String mostrarCategorias(Model model) {
        try {
            Categoria[] response = restTemplate.getForObject(CATEGORIAS_URL, Categoria[].class);
            model.addAttribute("categorias", Arrays.asList(response != null ? response : new Categoria[0]));
        } catch (Exception e) {
            model.addAttribute("categorias", new ArrayList<>());
        }
        return "categorias";
    }

    @GetMapping("/categoria/nueva")
    public String nuevaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categoria-form";
    }

    @GetMapping("/categoria/editar/{id}")
    public String editarCategoria(@PathVariable("id") Integer id, Model model) {
        Categoria categoria = restTemplate.getForObject(CATEGORIAS_URL + "/" + id, Categoria.class);
        model.addAttribute("categoria", categoria);
        return "categoria-form"; // Reutilizamos el mismo formulario, Thymeleaf es inteligente
    }

    @PostMapping("/categoria/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        if (categoria.getIdCategoria() == null) {
            restTemplate.postForObject(CATEGORIAS_URL, categoria, Categoria.class);
        } else {
            restTemplate.put(CATEGORIAS_URL + "/" + categoria.getIdCategoria(), categoria);
        }
        return "redirect:/categorias";
    }

    @GetMapping("/categoria/eliminar/{id}")
    public String eliminarCategoria(@PathVariable("id") Integer id) {
        try { restTemplate.delete(CATEGORIAS_URL + "/" + id); } 
        catch (Exception e) { System.out.println("Error: Categoría en uso."); }
        return "redirect:/categorias";
    }

    // ==========================================
    // 4. USUARIOS
    // ==========================================
    @GetMapping("/usuarios")
    public String mostrarUsuarios(Model model) {
        try {
            // Hacemos la llamada
            Usuario[] response = restTemplate.getForObject(USUARIOS_URL, Usuario[].class);
            model.addAttribute("usuarios", Arrays.asList(response != null ? response : new Usuario[0]));
            System.out.println("✅ ÉXITO: Se recibieron " + (response != null ? response.length : 0) + " usuarios.");
        } catch (Exception e) {
            // ESTA LÍNEA ES CLAVE
            System.out.println("❌ ERROR AL LISTAR USUARIOS: " + e.getMessage());
            e.printStackTrace(); // Esto nos dirá si es un problema de conexión, timeout, etc.
            model.addAttribute("usuarios", new ArrayList<>());
        }
        return "usuarios";
    }

    @GetMapping("/usuario/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-form";
    }

    @GetMapping("/usuario/editar/{id}")
    public String editarUsuario(@PathVariable("id") Integer id, Model model) {
        try {
            Usuario usuario = restTemplate.getForObject(USUARIOS_URL + "/" + id, Usuario.class);
            model.addAttribute("usuario", usuario);
            return "usuario-form";
        } catch (Exception e) {
            System.out.println("❌ ERROR AL EDITAR USUARIO: " + e.getMessage());
            return "redirect:/usuarios"; 
        }
    }

    @PostMapping("/usuario/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        try {
            if (usuario.getIdUsuario() == null) {
                restTemplate.postForObject(USUARIOS_URL, usuario, Usuario.class);
            } else {
                restTemplate.put(USUARIOS_URL + "/" + usuario.getIdUsuario(), usuario);
            }
            return "redirect:/usuarios";
        } catch (Exception e) {
            // ESTO NOS DARÁ LA RESPUESTA REAL DEL SERVIDOR
            System.out.println("❌ ERROR AL GUARDAR USUARIO: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    @GetMapping("/usuario/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id) {
        try { restTemplate.delete(USUARIOS_URL + "/" + id); } 
        catch (Exception e) { System.out.println("Error: Usuario en uso."); }
        return "redirect:/usuarios";
    }

    // ==========================================
    // 5. PEDIDOS
    // ==========================================
    @GetMapping("/pedidos")
    public String mostrarPedidos(Model model) {
        try {
            // 1. Traemos los pedidos
            Pedido[] response = restTemplate.getForObject(PEDIDOS_URL, Pedido[].class);
            List<Pedido> pedidos = response != null ? Arrays.asList(response) : new ArrayList<>();
            
            // 2. Traemos TODOS los usuarios para crear el "diccionario" de nombres
            Usuario[] usersResponse = restTemplate.getForObject(USUARIOS_URL, Usuario[].class);
            
            // 3. Creamos un Mapa (ID -> Nombre)
            Map<Long, String> usuariosMap = new HashMap<>();
            if (usersResponse != null) {
                for (Usuario u : usersResponse) {
                    usuariosMap.put(u.getIdUsuario().longValue(), u.getNombre());
                }
            }
            
            // 4. Enviamos ambos al modelo
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("usuariosMap", usuariosMap);
            
        } catch (Exception e) {
            model.addAttribute("pedidos", new ArrayList<>());
            model.addAttribute("usuariosMap", new HashMap<>());
            System.out.println("❌ ERROR: " + e.getMessage());
        }
        return "pedidos";
    }

    @GetMapping("/pedido/nuevo")
    public String nuevoPedido(Model model) {
        Pedido p = new Pedido(); // Ya no necesita p.setUsuario() porque ahora es un número directo
        model.addAttribute("pedido", p);
        
        try {
            Usuario[] users = restTemplate.getForObject(USUARIOS_URL, Usuario[].class);
            model.addAttribute("usuarios", users != null ? Arrays.asList(users) : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("usuarios", new ArrayList<>());
        }
        return "pedido-form";
    }

    @GetMapping("/pedido/editar/{id}")
    public String editarPedido(@PathVariable("id") Integer id, Model model) {
        try {
            Pedido pedido = restTemplate.getForObject(PEDIDOS_URL + "/" + id, Pedido.class);
            model.addAttribute("pedido", pedido);
            // Cargar usuarios para el selector
            Usuario[] users = restTemplate.getForObject(USUARIOS_URL, Usuario[].class);
            model.addAttribute("usuarios", users != null ? Arrays.asList(users) : new ArrayList<>());
            return "pedido-form";
        } catch (Exception e) {
            System.out.println("❌ ERROR AL EDITAR PEDIDO: " + e.getMessage());
            e.printStackTrace(); // Esto nos dará la línea exacta del error en la terminal
            return "redirect:/pedidos";
        }
    }

    @PostMapping("/pedido/guardar")
    public String guardarPedido(@ModelAttribute Pedido pedido) {
        // ESTA LÍNEA TE DIRÁ SI EL ID LLEGA O NO
        System.out.println("DEBUG FINAL: ID Usuario recibido: " + pedido.getIdUsuario()); 
        
        if (pedido.getIdPedido() == null) {
            restTemplate.postForObject(PEDIDOS_URL, pedido, Pedido.class);
        } else {
            restTemplate.put(PEDIDOS_URL + "/" + pedido.getIdPedido(), pedido);
        }
        return "redirect:/pedidos";
    }

    @GetMapping("/pedido/eliminar/{id}")
    public String eliminarPedido(@PathVariable("id") Integer id) {
        try {
            restTemplate.delete(PEDIDOS_URL + "/" + id);
            System.out.println("✅ Pedido eliminado: " + id);
        } catch (Exception e) {
            // CAMBIO: Imprimimos el error real del servidor
            System.out.println("❌ ERROR AL ELIMINAR. RESPUESTA DEL BACKEND: " + e.getMessage()); 
        }
        return "redirect:/pedidos";
    }
}
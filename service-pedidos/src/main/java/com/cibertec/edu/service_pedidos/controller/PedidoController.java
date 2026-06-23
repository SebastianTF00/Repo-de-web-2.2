package com.cibertec.edu.service_pedidos.controller;

import com.cibertec.edu.service_pedidos.model.DetallePedido;
import com.cibertec.edu.service_pedidos.model.Pedido;
import com.cibertec.edu.service_pedidos.repository.PedidoRepository;
import com.cibertec.edu.service_pedidos.client.UsuarioClient;
import com.cibertec.edu.service_pedidos.client.ProductoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private ProductoClient productoClient; // Tu nuevo cliente Feign

    // 1. Obtener todos los pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        return new ResponseEntity<>(pedidoRepository.findAll(), HttpStatus.OK);
    }

    // Agrega esto debajo de listarTodos()
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable("id") Integer id) {
        return pedidoRepository.findById(id)
                .map(pedido -> new ResponseEntity<>(pedido, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 2. CREAR UN PEDIDO (El Mega POST)
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        try {
            // PASO 1: Validar que el usuario exista (Llama a service-usuarios)
            // Si el usuario no existe, Feign lanzará un error y saltará al catch
            usuarioClient.obtenerUsuarioPorId(pedido.getIdUsuario().intValue());

            BigDecimal totalCalculado = BigDecimal.ZERO;

            // PASO 2: Procesar los productos del pedido
            if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    
                    // Llama a service-productos para traer los datos reales
                    Object responseProd = productoClient.obtenerProductoPorId(detalle.getIdProducto().intValue());
                    
                    // Convertimos la respuesta a un Map para extraer el precio fácilmente
                    Map<String, Object> productoData = (Map<String, Object>) responseProd;
                    
                    // Extraemos el precio de la base de datos (NUNCA del Frontend por seguridad)
                    BigDecimal precioReal = new BigDecimal(productoData.get("precio").toString());

                    // Asignamos el precio real al detalle
                    detalle.setPrecioUnitario(precioReal);

                    // Conectamos el detalle con el pedido (relación bidireccional)
                    detalle.setPedido(pedido);

                    // Calculamos subtotal: precio * cantidad y lo sumamos al total
                    BigDecimal subtotal = precioReal.multiply(new BigDecimal(detalle.getCantidad()));
                    totalCalculado = totalCalculado.add(subtotal);
                }
            } else {
                return ResponseEntity.badRequest().body("El pedido debe tener al menos un producto.");
            }

            // PASO 3: Setear totales y estado por defecto
            pedido.setTotal(totalCalculado);
            if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
                pedido.setEstado("PENDIENTE");
}

            // PASO 4: Guardar en la base de datos (Guarda el Pedido y sus Detalles automáticamente)
            Pedido nuevoPedido = pedidoRepository.save(pedido);

            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);

        } catch (Exception e) {
            // Si algún Feign falla (ej. Usuario 99 no existe) u ocurre otro error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido. Verifica que el usuario y los productos existan. Detalle: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable("id") Integer id, @RequestBody Pedido pedidoDetalles) {
        if (!pedidoRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        pedidoDetalles.setIdPedido(id);
        return new ResponseEntity<>(pedidoRepository.save(pedidoDetalles), HttpStatus.OK);
    }

    // ... dentro de PedidoController.java ...

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {
        if (!pedidoRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        pedidoRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
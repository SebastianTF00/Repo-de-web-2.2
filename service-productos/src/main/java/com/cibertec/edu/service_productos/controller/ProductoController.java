package com.cibertec.edu.service_productos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cibertec.edu.service_productos.model.Producto;
import com.cibertec.edu.service_productos.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permite que el Frontend se conecte sin bloqueos de CORS
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 1. Obtener todos los productos (Para el catálogo principal)
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    // 2. Obtener un solo producto por su ID (Para la vista de detalles)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable("id") Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);
        
        return producto.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 3. Filtrar productos por categoría
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Producto>> listarPorCategoria(@PathVariable("idCategoria") Integer idCategoria) {
        List<Producto> productos = productoRepository.findByCategoriaIdCategoria(idCategoria);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    // 4. Guardar o crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        Producto nuevoProducto = productoRepository.save(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // 5. Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {
        if (!productoRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productoRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
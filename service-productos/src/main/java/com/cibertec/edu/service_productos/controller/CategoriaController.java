package com.cibertec.edu.service_productos.controller;

import com.cibertec.edu.service_productos.model.Categoria;
import com.cibertec.edu.service_productos.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias") // Ruta base estandarizada
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodos() {
        return new ResponseEntity<>(categoriaRepository.findAll(), HttpStatus.OK);
    }

    // 2. Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable("id") Integer id) {
        return categoriaRepository.findById(id)
                .map(categoria -> new ResponseEntity<>(categoria, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 3. Guardar una nueva categoría
    @PostMapping
    public ResponseEntity<Categoria> guardar(@RequestBody Categoria categoria) {
        return new ResponseEntity<>(categoriaRepository.save(categoria), HttpStatus.CREATED);
    }

    // 4. Eliminar una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) {
        if (!categoriaRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        categoriaRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
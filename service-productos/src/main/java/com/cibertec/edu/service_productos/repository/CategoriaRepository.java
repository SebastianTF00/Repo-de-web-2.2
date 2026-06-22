package com.cibertec.edu.service_productos.repository;

import com.cibertec.edu.service_productos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Aquí ya tenemos listos métodos como save(), findAll(), findById(), deleteById()
}

//test

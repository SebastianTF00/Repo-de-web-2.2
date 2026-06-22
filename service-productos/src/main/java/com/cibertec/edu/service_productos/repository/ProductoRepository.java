package com.cibertec.edu.service_productos.repository;

import com.cibertec.edu.service_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Metodo personalizado para filtrar componentes de hardware por su categoría en el E-commerce
    List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
}
package com.cibertec.edu.service_pedidos.repository;

import com.cibertec.edu.service_pedidos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido , Integer> {
    // Puedes agregar consultas personalizadas si las necesitas
}
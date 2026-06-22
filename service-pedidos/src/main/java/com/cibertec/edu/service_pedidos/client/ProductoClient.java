package com.cibertec.edu.service_pedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// LE DECIMOS A FEIGN : "OYE BUSCA A SERVICE-PRODUCTOS EN EUREKA Y CONECTATE"
@FeignClient(name = "service-productos")
public interface ProductoClient {

    // RUTA EXACTA DEL CONTROLLADOR PRODUCTOS
    @GetMapping("/api/productos/{id}")
    Object obtenerProductoPorId(@PathVariable("id") Integer id);
}
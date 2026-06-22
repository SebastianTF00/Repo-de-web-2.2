package com.cibertec.edu.service_pedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//LE DECIMOS A FEIGN : "OYE BUSCA A SERVICE-USUARIOS EN EUREKA Y CONECTATE"
@FeignClient(name = "service-usuarios")
public interface UsuarioClient {
    
    //ACA LITERALMENTE LE COPIAMOS LA FIRMA DEL METODO USUARIOCONTROLLER
    @GetMapping("/api/usuarios/{id}")
    Object obtenerUsuarioPorId(@PathVariable("id") Integer id);

}

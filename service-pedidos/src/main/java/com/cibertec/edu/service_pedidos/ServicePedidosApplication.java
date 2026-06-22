package com.cibertec.edu.service_pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; //<-- IMPORTANDO EL TELEFONO (OPEN FEIGN)

@SpringBootApplication
@EnableFeignClients //<----- INSERTANDO EL TELEFONO (OPEN FEIGN)
public class ServicePedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicePedidosApplication.class, args);
	}

}

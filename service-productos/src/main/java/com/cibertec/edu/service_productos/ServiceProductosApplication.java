package com.cibertec.edu.service_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductosApplication.class, args);
    }
}

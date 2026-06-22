package com.cibertec.edu.service_frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceFrontendApplication {
    public static void main(String[] args){
        SpringApplication.run(ServiceFrontendApplication.class, args);
    }
}
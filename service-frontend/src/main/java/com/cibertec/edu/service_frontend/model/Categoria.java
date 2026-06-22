package com.cibertec.edu.service_frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Integer idCategoria;
    private String nombre;
    private String descripcion;

    // Métodos explícitos para asegurar que Thymeleaf los vea
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
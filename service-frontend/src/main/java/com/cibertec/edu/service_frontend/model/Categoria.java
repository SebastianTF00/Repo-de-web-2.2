package com.cibertec.edu.service_frontend.model;

public class Categoria {
    private Integer idCategoria;
    private String nombre;
    private String descripcion;

    // Métodos explícitos para asegurar que Thymeleaf los vea
    public Integer getIdCategoria(){
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public void setIdCategoria(Integer idCategoria){
        this.idCategoria = idCategoria;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    
}
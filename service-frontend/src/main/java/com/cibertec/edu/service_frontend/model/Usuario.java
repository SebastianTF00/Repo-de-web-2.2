package com.cibertec.edu.service_frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String contrasenia;
    private String rol;

    // Constructor vacío
    public Usuario() {}

    // Getters y Setters explícitos
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
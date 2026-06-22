package com.cibertec.edu.service_frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String contrasenia;
    private String rol;
    private LocalDateTime fechaRegistro;
}
package com.cibertec.edu.service_usuarios.repository;

import com.cibertec.edu.service_usuarios.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    //METODO PARA BUSCAR AL USUARIO POR SU CORREO EN EL LOGIN
    Optional<Usuario> findByCorreo(String correo);
}
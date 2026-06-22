package com.cibertec.edu.service_usuarios.repository;

import com.cibertec.edu.service_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Listo para usarse. Más adelante puedes agregar: Optional<Usuario> findByCorreo(String correo);
}
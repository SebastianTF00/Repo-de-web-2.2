package com.cibertec.edu.service_usuarios.controller;

import com.cibertec.edu.service_usuarios.model.Usuario;
import com.cibertec.edu.service_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//IMPORTS PARA BYSCRYPT
import org.mindrot.jbcrypt.BCrypt;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite peticiones desde tu Frontend
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Obtener la lista de usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return new ResponseEntity<>(usuarioRepository.findAll(), HttpStatus.OK);
    }

    // 2. Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable("id") Integer id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        
        return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 3. Guardar o Actualizar Usuario
    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("CLIENTE");
        }
        
        // ¡LA MAGIA DE BCRYPT!: Encriptamos la contraseña antes de guardarla en MySQL
        String hash = BCrypt.hashpw(usuario.getContrasenia(), BCrypt.gensalt());
        usuario.setContrasenia(hash);
        
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // 4. Eliminar Usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("id") Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        usuarioRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 5. Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable("id") Integer id, @RequestBody Usuario usuarioDetalles) {
        if (!usuarioRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        usuarioDetalles.setIdUsuario(id);
        return new ResponseEntity<>(usuarioRepository.save(usuarioDetalles), HttpStatus.OK);
    }

    // 6. NUEVO: Validar Login Real
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasenia = credenciales.get("contrasenia");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparamos la contraseña escrita en el HTML con la encriptada en MySQL
            if (BCrypt.checkpw(contrasenia, usuario.getContrasenia())) {
                return new ResponseEntity<>(usuario, HttpStatus.OK);
            }
        }
        // Si no existe el correo o la clave no coincide, mandamos error 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
}
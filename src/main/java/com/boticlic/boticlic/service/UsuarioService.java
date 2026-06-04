package com.boticlic.boticlic.service;

import com.boticlic.boticlic.model.Usuario;
import com.boticlic.boticlic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya se encuentra registrado");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        // Solo asignar CLIENTE si no viene un rol
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ Actualizar nombre si viene
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            usuario.setNombre(datos.getNombre());
        }

        // ✅ Actualizar email si viene y no está en uso por otro usuario
        if (datos.getEmail() != null && !datos.getEmail().isBlank()) {
            boolean emailEnUso = usuarioRepository.findByEmail(datos.getEmail())
                    .map(u -> !u.getId().equals(id))
                    .orElse(false);
            if (emailEnUso) {
                throw new RuntimeException("El correo ya está en uso por otro usuario");
            }
            usuario.setEmail(datos.getEmail());
        }

        // ✅ Actualizar password si viene — encriptarla con BCrypt
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        // ✅ Actualizar rol si viene
        if (datos.getRol() != null && !datos.getRol().isBlank()) {
            usuario.setRol(datos.getRol());
        }

        // ✅ Actualizar estado activo
        usuario.setActivo(datos.isActivo());

        // ✅ Actualizar datos opcionales del perfil
        if (datos.getCelular() != null) {
            usuario.setCelular(datos.getCelular());
        }
        if (datos.getTipoDocumento() != null) {
            usuario.setTipoDocumento(datos.getTipoDocumento());
        }
        if (datos.getNumeroDocumento() != null) {
            usuario.setNumeroDocumento(datos.getNumeroDocumento());
        }

        return usuarioRepository.save(usuario);
    }
}
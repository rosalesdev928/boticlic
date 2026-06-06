package com.boticlic.boticlic.repository;

import com.boticlic.boticlic.model.Suscriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SuscriptorRepository extends JpaRepository<Suscriptor, Long> {

    // Para verificar si el correo ya está suscrito
    boolean existsByEmail(String email);

    // Para buscar un suscriptor por su correo (ej: para desuscribirse)
    Optional<Suscriptor> findByEmail(String email);

    // Para listar solo los activos (útil en el admin para enviar campañas)
    List<Suscriptor> findByActivoTrue();
}
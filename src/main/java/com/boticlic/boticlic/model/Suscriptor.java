package com.boticlic.boticlic.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "newsletter_suscriptores")
public class Suscriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true evita que el mismo correo se suscriba dos veces
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime fechaSuscripcion = LocalDateTime.now();

    // Para poder desactivar suscriptores sin borrarlos
    @Column(nullable = false)
    private boolean activo = true;
}
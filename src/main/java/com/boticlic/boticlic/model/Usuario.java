package com.boticlic.boticlic.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // ADMIN, CLIENTE, FARMACEUTICO, DELIVERY

    @Column(nullable = false)
    private boolean activo = true;

    // Nuevos campos
    private String tipoDocumento;   // DNI, CE, PASAPORTE

    private String numeroDocumento;

    private String celular;

    private String fechaNacimiento; // formato DD/MM/AAAA

    private String genero;          // M, F, OTRO
}
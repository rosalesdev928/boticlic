package com.boticlic.boticlic.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Usuario repartidor;

    @Column(nullable = false)
    private String estado; // ASIGNADO, EN_CAMINO, ENTREGADO

    private LocalDateTime fechaAsignacion;

    private LocalDateTime fechaEntrega;

    private String direccionEntrega;

    private Double latitud;

    private Double longitud;
}
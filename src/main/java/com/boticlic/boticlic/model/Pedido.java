package com.boticlic.boticlic.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    @Column(nullable = false)
    private String estado; // PENDIENTE, CONFIRMADO, EN_PREPARACION, EN_CAMINO, ENTREGADO

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private Double total;

    private String direccionEntrega;

    private String metodoPago;
}
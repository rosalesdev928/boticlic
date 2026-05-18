package com.boticlic.boticlic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    // ✅ Evita serializar todo el usuario (evita ciclo y oculta password)
    @JsonIgnoreProperties({"password", "activo"})
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ✅ Evita que DetallePedido vuelva a serializar el Pedido (ciclo infinito)
    @JsonIgnoreProperties("pedido")
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private Double total;
    private String direccionEntrega;
    private String metodoPago;
}
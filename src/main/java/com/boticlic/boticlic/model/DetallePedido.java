package com.boticlic.boticlic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_pedidos")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Ignora el pedido al serializar (corta el ciclo Pedido → Detalle → Pedido)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    // ✅ Solo serializa datos básicos del producto
    @JsonIgnoreProperties({"descripcion", "stock", "stockMinimo", "disponible", "imagen"})
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;

    public Double getSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            return cantidad * precioUnitario;
        }
        return 0.0;
    }
}
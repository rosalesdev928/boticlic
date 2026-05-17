package com.boticlic.boticlic.repository;

import com.boticlic.boticlic.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByRepartidorId(Long repartidorId);
    List<Delivery> findByEstado(String estado);
    Optional<Delivery> findByPedidoId(Long pedidoId);
}
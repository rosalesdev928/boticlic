package com.boticlic.boticlic.repository;

import com.boticlic.boticlic.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByEstado(String estado);
    List<Receta> findByUsuarioId(Long usuarioId);
    List<Receta> findByPedidoId(Long pedidoId);
    List<Receta> findByEstadoOrderByFechaEnvioDesc(String estado);
}
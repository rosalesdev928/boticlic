package com.boticlic.boticlic.repository;

import com.boticlic.boticlic.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaIgnoreCase(String categoria);
    List<Producto> findByDisponibleTrue();
    List<Producto> findByStockLessThanEqual(Integer stockMinimo);
}
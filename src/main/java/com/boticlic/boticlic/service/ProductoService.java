package com.boticlic.boticlic.service;

import com.boticlic.boticlic.model.Producto;
import com.boticlic.boticlic.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findByDisponibleTrue();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    public Producto guardar(Producto producto) {
        // ✅ Validar precio y stock no negativos
        if (producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        if (producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        // ✅ Stock mínimo por defecto si no viene
        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(5);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setCategoria(datos.getCategoria());
        producto.setImagen(datos.getImagen());
        producto.setDisponible(datos.isDisponible());
        // ✅ También actualizar stockMinimo
        if (datos.getStockMinimo() != null) {
            producto.setStockMinimo(datos.getStockMinimo());
        }
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setDisponible(false);
        productoRepository.save(producto);
    }

    public List<Producto> productosConStockBajo() {
        // ✅ CORREGIDO: usa el stockMinimo de cada producto en lugar del 5 fijo
        return productoRepository.findAll().stream()
                .filter(p -> p.isDisponible()) // Solo productos activos
                .filter(p -> {
                    int minimo = (p.getStockMinimo() != null) ? p.getStockMinimo() : 5;
                    return p.getStock() <= minimo;
                })
                .sorted((a, b) -> {
                    // Ordenar: agotados primero, luego por stock ascendente
                    if (a.getStock() == 0 && b.getStock() > 0) return -1;
                    if (b.getStock() == 0 && a.getStock() > 0) return 1;
                    return Integer.compare(a.getStock(), b.getStock());
                })
                .collect(Collectors.toList());
    }
}
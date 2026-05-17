package com.boticlic.boticlic.service;

import com.boticlic.boticlic.model.Delivery;
import com.boticlic.boticlic.model.Pedido;
import com.boticlic.boticlic.model.Usuario;
import com.boticlic.boticlic.repository.DeliveryRepository;
import com.boticlic.boticlic.repository.PedidoRepository;
import com.boticlic.boticlic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public Delivery asignarRepartidor(Long pedidoId, Long repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        Usuario repartidor = usuarioRepository.findById(repartidorId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        Delivery delivery = new Delivery();
        delivery.setPedido(pedido);
        delivery.setRepartidor(repartidor);
        delivery.setEstado("ASIGNADO");
        delivery.setFechaAsignacion(LocalDateTime.now());
        delivery.setDireccionEntrega(pedido.getDireccionEntrega());

        pedido.setEstado("EN_CAMINO");
        pedidoRepository.save(pedido);

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> listarPorRepartidor(Long repartidorId) {
        return deliveryRepository.findByRepartidorId(repartidorId);
    }

    public Delivery actualizarEstado(Long id, String estado, Double latitud, Double longitud) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery no encontrado"));
        delivery.setEstado(estado);
        if (latitud != null) delivery.setLatitud(latitud);
        if (longitud != null) delivery.setLongitud(longitud);
        if (estado.equals("ENTREGADO")) {
            delivery.setFechaEntrega(LocalDateTime.now());
            delivery.getPedido().setEstado("ENTREGADO");
            pedidoRepository.save(delivery.getPedido());
            notificacionService.notificarCambioDEstado(
                    delivery.getPedido().getUsuario().getEmail(),
                    String.valueOf(delivery.getPedido().getId()),
                    "ENTREGADO"
            );
        }
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> listarPorEstado(String estado) {
        return deliveryRepository.findByEstado(estado);
    }
}
package com.cibertec.edu.service_frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Integer idPedido;
    private Long idUsuario;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private String estado;
    private List<DetallePedido> detalles;
}
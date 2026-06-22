package com.cibertec.edu.service_frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
    private Integer idDetalle;
    private Pedido pedido;
    private Long idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
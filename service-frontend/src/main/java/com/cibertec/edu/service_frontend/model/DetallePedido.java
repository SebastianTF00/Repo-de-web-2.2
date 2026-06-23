package com.cibertec.edu.service_frontend.model;

import java.math.BigDecimal;

public class DetallePedido {
    private Integer idDetalle;
    private Pedido pedido;
    private Integer idProducto; // Lo puse como Integer para que coincida con Producto.java
    private Integer cantidad;
    private BigDecimal precioUnitario;

    // Constructor vacío
    public DetallePedido() {}

    // Getters y Setters explícitos y PUBLICOS
    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}
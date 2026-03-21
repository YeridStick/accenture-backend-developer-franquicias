package co.franquicias.api.dto.producto;

public record UpdateProductoRequest(String nombre, Integer stock, Long precio, String sucursalId) {}
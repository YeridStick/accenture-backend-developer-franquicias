package co.franquicias.api.dto.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public record CreateProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,

        @Min(value = 0, message = "El stock no puede ser negativo")
        int stock
) {}

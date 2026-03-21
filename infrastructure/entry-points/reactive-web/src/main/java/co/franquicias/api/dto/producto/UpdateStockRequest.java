package co.franquicias.api.dto.producto;

import jakarta.validation.constraints.Min;

public record UpdateStockRequest(
        @Min(value = 0, message = "El stock no puede ser negativo")
        int stock
) {}

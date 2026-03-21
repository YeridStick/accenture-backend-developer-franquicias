package co.franquicias.api.dto.sucursal;

import jakarta.validation.constraints.NotBlank;

public record CreateSucursalRequest(
        @NotBlank(message = "El nombre de la sucursal es obligatorio")
        String nombre
) {}
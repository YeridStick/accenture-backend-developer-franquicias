package co.franquicias.api.dto.franquicia;

import jakarta.validation.constraints.NotBlank;

public record CreateFranquiciaRequest(
        @NotBlank(message = "El nombre de la franquicia es obligatorio")
        String nombre
) {}
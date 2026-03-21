package co.franquicias.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestConstants {

    // Rutas — Franquicias
    public static final String BASE_FRANQUICIAS              = "/api/franquicias";
    public static final String FRANQUICIA_BY_NAME            = "/api/franquicias/by-name";
    public static final String FRANQUICIA_BY_ID              = "/api/franquicias/{franquiciaId}";
    public static final String FRANQUICIA_MAX_STOCK          = "/api/franquicias/{franquiciaId}/max-stock-por-sucursal";

    // Rutas — Sucursales
    public static final String SUCURSALES_DE_FRANQUICIA      = "/api/franquicias/{franquiciaId}/sucursales";
    public static final String SUCURSAL_BY_ID                = "/api/sucursales/{sucursalId}";

    // Rutas — Productos
    public static final String BASE_PRODUCTOS                = "/api/productos";
    public static final String PRODUCTOS_SEARCH              = "/api/productos/search";
    public static final String PRODUCTOS_VIEW                = "/api/productos/view";
    public static final String PRODUCTO_VIEW_BY_ID           = "/api/productos/view/{productoId}";
    public static final String PRODUCTO_BY_ID                = "/api/productos/{productoId}";
    public static final String PRODUCTOS_DE_SUCURSAL         = "/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos";
    public static final String PRODUCTO_DE_SUCURSAL_BY_ID    = "/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}";
    public static final String PRODUCTO_STOCK                = "/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock";
}

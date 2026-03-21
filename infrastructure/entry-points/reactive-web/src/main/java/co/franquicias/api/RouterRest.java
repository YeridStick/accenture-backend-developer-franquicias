package co.franquicias.api;

import co.franquicias.api.constants.RestConstants;
import co.franquicias.api.dto.franquicia.CreateFranquiciaRequest;
import co.franquicias.api.dto.franquicia.UpdateFranquiciaRequest;
import co.franquicias.api.dto.producto.CreateProductoRequest;
import co.franquicias.api.dto.producto.UpdateStockRequest;
import co.franquicias.api.dto.producto.UpdateProductoRequest;
import co.franquicias.api.dto.sucursal.CreateSucursalRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final Handler handler;

    @Bean
    @RouterOperations({

        // --- Franquicias -----------------------------------------------------
        @RouterOperation(
            path = RestConstants.BASE_FRANQUICIAS, method = RequestMethod.POST,
            beanClass = Handler.class, beanMethod = "crearFranquicia",
            operation = @Operation(
                operationId = "crearFranquicia", summary = "Crear nueva franquicia", tags = {"Franquicias"},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateFranquiciaRequest.class)))
            )
        ),
        @RouterOperation(
            path = RestConstants.BASE_FRANQUICIAS, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "obtenerFranquicias",
            operation = @Operation(
                operationId = "obtenerFranquicias", summary = "Listar todas las franquicias", tags = {"Franquicias"}
            )
        ),
        @RouterOperation(
            path = RestConstants.FRANQUICIA_BY_NAME, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "obtenerFranquiciaPorNombre",
            operation = @Operation(
                operationId = "obtenerFranquiciaPorNombre", summary = "Buscar franquicia por nombre", tags = {"Franquicias"},
                parameters = {@Parameter(name = "nombre", in = ParameterIn.QUERY)}
            )
        ),
        @RouterOperation(
            path = RestConstants.FRANQUICIA_BY_ID, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "obtenerFranquicia",
            operation = @Operation(
                operationId = "obtenerFranquicia", summary = "Obtener detalle de franquicia", tags = {"Franquicias"},
                parameters = {@Parameter(name = "franquiciaId", in = ParameterIn.PATH)}
            )
        ),
        @RouterOperation(
            path = RestConstants.FRANQUICIA_BY_ID, method = RequestMethod.PATCH,
            beanClass = Handler.class, beanMethod = "actualizarFranquicia",
            operation = @Operation(
                operationId = "actualizarFranquicia", summary = "Actualizar franquicia", tags = {"Franquicias"},
                parameters = {@Parameter(name = "franquiciaId", in = ParameterIn.PATH)},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateFranquiciaRequest.class)))
            )
        ),
        @RouterOperation(
            path = RestConstants.FRANQUICIA_BY_ID, method = RequestMethod.DELETE,
            beanClass = Handler.class, beanMethod = "eliminarFranquicia",
            operation = @Operation(
                operationId = "eliminarFranquicia", summary = "Eliminar franquicia", tags = {"Franquicias"},
                parameters = {@Parameter(name = "franquiciaId", in = ParameterIn.PATH)}
            )
        ),

        // --- Sucursales
        @RouterOperation(
            path = RestConstants.SUCURSALES_DE_FRANQUICIA, method = RequestMethod.POST,
            beanClass = Handler.class, beanMethod = "agregarSucursal",
            operation = @Operation(
                operationId = "agregarSucursal", summary = "Agregar sucursal", tags = {"Sucursales"},
                parameters = {@Parameter(name = "franquiciaId", in = ParameterIn.PATH)},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateSucursalRequest.class)))
            )
        ),
        @RouterOperation(
            path = RestConstants.SUCURSALES_DE_FRANQUICIA, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "listarSucursalesDeFranquicia",
            operation = @Operation(
                operationId = "listarSucursales", summary = "Listar sucursales de una franquicia", tags = {"Sucursales"},
                parameters = {@Parameter(name = "franquiciaId", in = ParameterIn.PATH)}
            )
        ),
        @RouterOperation(
            path = RestConstants.SUCURSAL_BY_ID, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "obtenerSucursal",
            operation = @Operation(
                operationId = "obtenerSucursal", summary = "Obtener sucursal por ID", tags = {"Sucursales"},
                parameters = {@Parameter(name = "sucursalId", in = ParameterIn.PATH)}
            )
        ),
        @RouterOperation(
            path = RestConstants.SUCURSAL_BY_ID, method = RequestMethod.PATCH,
            beanClass = Handler.class, beanMethod = "actualizarSucursal",
            operation = @Operation(
                operationId = "actualizarSucursal", summary = "Actualizar sucursal", tags = {"Sucursales"},
                parameters = {@Parameter(name = "sucursalId", in = ParameterIn.PATH)}
            )
        ),
        @RouterOperation(
            path = RestConstants.SUCURSAL_BY_ID, method = RequestMethod.DELETE,
            beanClass = Handler.class, beanMethod = "eliminarSucursal",
            operation = @Operation(
                operationId = "eliminarSucursal", summary = "Eliminar sucursal", tags = {"Sucursales"},
                parameters = {@Parameter(name = "sucursalId", in = ParameterIn.PATH)}
            )
        ),

        // --- Productos
        @RouterOperation(
            path = RestConstants.PRODUCTOS_SEARCH, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "searchProductosGlobal",
            operation = @Operation(
                operationId = "searchProductos", summary = "Buscar productos globalmente", tags = {"Productos"},
                parameters = {
                    @Parameter(name = "nombreLike", in = ParameterIn.QUERY, description = "Texto a buscar"),
                    @Parameter(name = "page",       in = ParameterIn.QUERY, description = "Número de página (0+)"),
                    @Parameter(name = "size",       in = ParameterIn.QUERY, description = "Tamaño de página")
                }
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTOS_VIEW, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "getAllProductosView",
            operation = @Operation(
                operationId = "getAllProductosView", summary = "Listar todos los productos (Vista Detallada)", tags = {"Productos"},
                parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Número de página (0+)"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Tamaño de página")
                }
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTO_VIEW_BY_ID, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "getProductoGlobalView",
            operation = @Operation(
                operationId = "getProductoGlobalView", summary = "Obtener vista detallada de un producto", tags = {"Productos"},
                parameters = {@Parameter(name = "productoId", in = ParameterIn.PATH)}
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTO_BY_ID, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "getProductoGlobal",
            operation = @Operation(
                operationId = "getProductoGlobal", summary = "Obtener producto por ID (Datos Crudos)", tags = {"Productos"},
                parameters = {@Parameter(name = "productoId", in = ParameterIn.PATH)}
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTO_BY_ID, method = RequestMethod.PATCH,
            beanClass = Handler.class, beanMethod = "actualizarProducto",
            operation = @Operation(
                operationId = "actualizarProducto", summary = "Actualizar producto (Campos base)", tags = {"Productos"},
                parameters = {@Parameter(name = "productoId", in = ParameterIn.PATH)},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateProductoRequest.class)))
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTOS_DE_SUCURSAL, method = RequestMethod.POST,
            beanClass = Handler.class, beanMethod = "agregarProducto",
            operation = @Operation(
                operationId = "agregarProducto", summary = "Agregar producto", tags = {"Productos"},
                parameters = {
                    @Parameter(name = "franquiciaId", in = ParameterIn.PATH),
                    @Parameter(name = "sucursalId",   in = ParameterIn.PATH)
                },
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateProductoRequest.class)))
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTOS_DE_SUCURSAL, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "getProductosDeSucursal",
            operation = @Operation(
                operationId = "getProductosDeSucursal", summary = "Listar productos de una sucursal", tags = {"Productos"},
                parameters = {
                    @Parameter(name = "franquiciaId", in = ParameterIn.PATH),
                    @Parameter(name = "sucursalId",   in = ParameterIn.PATH),
                    @Parameter(name = "page",         in = ParameterIn.QUERY, description = "Página"),
                    @Parameter(name = "size",         in = ParameterIn.QUERY, description = "Tamaño")
                }
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTO_DE_SUCURSAL_BY_ID, method = RequestMethod.DELETE,
            beanClass = Handler.class, beanMethod = "eliminarProducto",
            operation = @Operation(
                operationId = "eliminarProducto", summary = "Eliminar producto de sucursal", tags = {"Productos"},
                parameters = {
                    @Parameter(name = "franquiciaId", in = ParameterIn.PATH),
                    @Parameter(name = "sucursalId",   in = ParameterIn.PATH),
                    @Parameter(name = "productoId",   in = ParameterIn.PATH)
                }
            )
        ),
        @RouterOperation(
            path = RestConstants.PRODUCTO_STOCK, method = RequestMethod.PATCH,
            beanClass = Handler.class, beanMethod = "actualizarStock",
            operation = @Operation(
                operationId = "actualizarStock", summary = "Actualizar stock de producto", tags = {"Productos"},
                parameters = {
                    @Parameter(name = "franquiciaId", in = ParameterIn.PATH),
                    @Parameter(name = "sucursalId",   in = ParameterIn.PATH),
                    @Parameter(name = "productoId",   in = ParameterIn.PATH)
                },
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateStockRequest.class)))
            )
        ),

        // --- Reportes
        @RouterOperation(
            path = RestConstants.FRANQUICIA_MAX_STOCK, method = RequestMethod.GET,
            beanClass = Handler.class, beanMethod = "maxStockPorSucursal",
            operation = @Operation(
                operationId = "maxStockPorSucursal", summary = "Producto con mayor stock por sucursal", tags = {"Reportes"},
                parameters = {@Parameter(name = "franquiciaId", in = ParameterIn.PATH)}
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction() {
        return route()

            // --- Franquicias
            .POST  (RestConstants.BASE_FRANQUICIAS,     handler::crearFranquicia)
            .GET   (RestConstants.BASE_FRANQUICIAS,     handler::obtenerFranquicias)
            .GET   (RestConstants.FRANQUICIA_BY_NAME,   handler::obtenerFranquiciaPorNombre)
            .GET   (RestConstants.FRANQUICIA_BY_ID,     handler::obtenerFranquicia)
            .PATCH (RestConstants.FRANQUICIA_BY_ID,     handler::actualizarFranquicia)
            .DELETE(RestConstants.FRANQUICIA_BY_ID,     handler::eliminarFranquicia)

            // --- Sucursales
            .POST  (RestConstants.SUCURSALES_DE_FRANQUICIA, handler::agregarSucursal)
            .GET   (RestConstants.SUCURSALES_DE_FRANQUICIA, handler::listarSucursalesDeFranquicia)
            .GET   (RestConstants.SUCURSAL_BY_ID,           handler::obtenerSucursal)
            .PATCH (RestConstants.SUCURSAL_BY_ID,           handler::actualizarSucursal)
            .DELETE(RestConstants.SUCURSAL_BY_ID,           handler::eliminarSucursal)

            // --- Productos
            .GET   (RestConstants.PRODUCTOS_SEARCH,         handler::searchProductosGlobal)
            .GET   (RestConstants.PRODUCTOS_VIEW,           handler::getAllProductosView)
            .GET   (RestConstants.PRODUCTO_VIEW_BY_ID,      handler::getProductoGlobalView)
            .GET   (RestConstants.BASE_PRODUCTOS,           handler::getAllProductos)
            .GET   (RestConstants.PRODUCTO_BY_ID,           handler::getProductoGlobal)
            .PATCH (RestConstants.PRODUCTO_BY_ID,           handler::actualizarProducto)

            // --- Productos por sucursal
            .POST  (RestConstants.PRODUCTOS_DE_SUCURSAL,        handler::agregarProducto)
            .GET   (RestConstants.PRODUCTOS_DE_SUCURSAL,        handler::getProductosDeSucursal)
            .DELETE(RestConstants.PRODUCTO_DE_SUCURSAL_BY_ID,   handler::eliminarProducto)
            .PATCH (RestConstants.PRODUCTO_STOCK,               handler::actualizarStock)

            // --- Reportes
            .GET   (RestConstants.FRANQUICIA_MAX_STOCK,     handler::maxStockPorSucursal)

            .build();
    }
}

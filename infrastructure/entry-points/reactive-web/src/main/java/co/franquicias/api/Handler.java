package co.franquicias.api;

import co.franquicias.api.dto.franquicia.CreateFranquiciaRequest;
import co.franquicias.api.dto.franquicia.UpdateFranquiciaRequest;
import co.franquicias.api.dto.producto.CreateProductoRequest;
import co.franquicias.api.dto.producto.ProductoViewDTO;
import co.franquicias.api.dto.producto.UpdateProductoRequest;
import co.franquicias.api.dto.producto.UpdateStockRequest;
import co.franquicias.api.dto.sucursal.CreateSucursalRequest;
import co.franquicias.api.dto.sucursal.UpdateSucursalRequest;
import co.franquicias.api.error.RequestValidator;
import co.franquicias.api.mapper.DtoMappers;
import co.franquicias.model.producto.Producto;
import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.model.franquicia.Franquicia;
import co.franquicias.usecase.franquicia.FranquiciaUseCase;
import co.franquicias.usecase.franquicia.ProductoUseCase;
import co.franquicias.usecase.franquicia.SucursalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final FranquiciaUseCase franquiciaUseCase;
    private final SucursalUseCase sucursalUseCase;
    private final ProductoUseCase productoUseCase;
    private final RequestValidator validator;

    // ---------- Franquicia ----------
    public Mono<ServerResponse> crearFranquicia(ServerRequest req) {
        return req.bodyToMono(CreateFranquiciaRequest.class)
                .doOnNext(validator::validate)
                .flatMap(body -> franquiciaUseCase.crearFranquicia(body.nombre().trim()))
                .flatMap(f -> ServerResponse
                        .created(URI.create("/api/franquicias/" + f.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(f));
    }

    public Mono<ServerResponse> obtenerFranquicias(ServerRequest req) {
        boolean verProducto = req.queryParam("includeProductos")
                .map(String::toLowerCase)
                .map(v -> v.equals("true") || v.equals("1") || v.equals("yes"))
                .orElse(false);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(franquiciaUseCase.obtenerFranquicias(verProducto), Franquicia.class);
    }

    public Mono<ServerResponse> obtenerFranquicia(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        return franquiciaUseCase.obtenerPorId(fId)
                .flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
    }

    public Mono<ServerResponse> obtenerFranquiciaPorNombre(ServerRequest req) {
        String nombre = req.queryParam("nombre").orElse("").trim();
        if (nombre.isBlank()) {
            return Mono.error(new IllegalArgumentException("El nombre es requerido para la consulta"));
        }
        return franquiciaUseCase.obtenerFranquiciaPorNombre(nombre)
                .flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
    }

    public Mono<ServerResponse> eliminarFranquicia(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        return franquiciaUseCase.eliminarFranquiciaPorId(fId)
                .flatMap(msg -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(Map.of("message", msg)));
    }

    public Mono<ServerResponse> actualizarFranquicia(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        return req.bodyToMono(UpdateFranquiciaRequest.class)
                .flatMap(b -> {
                    String n = b.nombre() != null ? b.nombre().trim() : null;
                    if (n != null && n.isBlank()) {
                        return Mono.error(new IllegalArgumentException("El nombre de la franquicia no puede estar vacío"));
                    }
                    return franquiciaUseCase.actualizarFranquicia(fId, Franquicia.builder().nombre(n).build());
                })
                .flatMap(f -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(f));
    }

    // ---------- Sucursal ----------
    public Mono<ServerResponse> agregarSucursal(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        return req.bodyToMono(CreateSucursalRequest.class)
                .doOnNext(validator::validate)
                .flatMap(b -> sucursalUseCase.agregarSucursal(fId, b.nombre().trim()))
                .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s));
    }

    public Mono<ServerResponse> listarSucursalesDeFranquicia(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sucursalUseCase.obtenerSucursalPorFranquiciaId(fId), Sucursal.class);
    }

    public Mono<ServerResponse> obtenerSucursal(ServerRequest req) {
        String sId = req.pathVariable("sucursalId");
        return sucursalUseCase.obtenerSucursalPorId(sId)
                .flatMap(suc -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(suc));
    }

    public Mono<ServerResponse> eliminarSucursal(ServerRequest req) {
        String sId = req.pathVariable("sucursalId");
        return sucursalUseCase.eliminarSucursalPorId(sId)
                .flatMap(msg -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(Map.of("message", msg)));
    }

    public Mono<ServerResponse> actualizarSucursal(ServerRequest req) {
        String sId = req.pathVariable("sucursalId");
        return req.bodyToMono(UpdateSucursalRequest.class)
                .flatMap(b -> {
                    String n = b.nombre() != null ? b.nombre().trim() : null;
                    if (n != null && n.isBlank()) {
                        return Mono.error(new IllegalArgumentException("El nombre de la sucursal no puede estar vacío"));
                    }
                    Sucursal patch = Sucursal.builder()
                            .nombre(n)
                            .franquiciaId(b.franquiciaId())
                            .build();
                    return sucursalUseCase.actualizarSucursal(sId, patch);
                })
                .flatMap(s -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(s));
    }

    // ---------- Producto ----------
    public Mono<ServerResponse> agregarProducto(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        String sId = req.pathVariable("sucursalId");
        return req.bodyToMono(CreateProductoRequest.class)
                .doOnNext(validator::validate)
                .flatMap(b -> productoUseCase.agregarProducto(fId, sId, b.nombre().trim(), b.stock()))
                .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p));
    }

    public Mono<ServerResponse> eliminarProducto(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        String sId = req.pathVariable("sucursalId");
        String pId = req.pathVariable("productoId");
        return productoUseCase.eliminarProducto(fId, sId, pId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> actualizarStock(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        String sId = req.pathVariable("sucursalId");
        String pId = req.pathVariable("productoId");
        return req.bodyToMono(UpdateStockRequest.class)
                .doOnNext(validator::validate)
                .flatMap(b -> productoUseCase.actualizarStock(fId, sId, pId, b.stock()))
                .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p));
    }

    public Mono<ServerResponse> actualizarProducto(ServerRequest req) {
        String pId = req.pathVariable("productoId");
        return req.bodyToMono(UpdateProductoRequest.class)
                .flatMap(b -> {
                    String n = b.nombre() != null ? b.nombre().trim() : null;
                    if (n != null && n.isBlank()) {
                        return Mono.error(new IllegalArgumentException("El nombre del producto no puede estar vacío"));
                    }
                    Integer stock = b.stock();
                    if (stock != null && stock < 0) {
                        return Mono.error(new IllegalArgumentException("Stock negativo no permitido"));
                    }
                    Producto patch = Producto.builder()
                            .nombre(n)
                            .stock(stock != null ? stock : 0)
                            .precio(b.precio())
                            .sucursalId(b.sucursalId())
                            .build();
                    return productoUseCase.actualizarProducto(pId, patch);
                })
                .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p));
    }

    // ---------- Reportes / consultas ----------
    public Mono<ServerResponse> maxStockPorSucursal(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoUseCase.maxStockPorSucursal(fId), Map.class);
    }

    public Mono<ServerResponse> getAllProductos(ServerRequest req) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoUseCase.getAllProductos(), Producto.class);
    }

    public Mono<ServerResponse> getProductoGlobal(ServerRequest req) {
        String pId = req.pathVariable("productoId");
        return productoUseCase.getProductoGlobal(pId)
                .flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p));
    }

    public Mono<ServerResponse> searchProductosGlobal(ServerRequest req) {
        String q = req.queryParam("nombreLike").map(String::trim).orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoUseCase.searchProductosGlobal(q)
                                .switchIfEmpty(productoUseCase.getAllProductos()),
                        Producto.class);
    }

    public Mono<ServerResponse> getAllProductosView(ServerRequest req) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        productoUseCase.getAllProductosViewRaw()
                                .map(o -> DtoMappers.toProductoViewDTO((Map<String,Object>) o)),
                        ProductoViewDTO.class
                );
    }

    public Mono<ServerResponse> getProductoGlobalView(ServerRequest req) {
        String pId = req.pathVariable("productoId");
        return productoUseCase.getProductoGlobalViewRaw(pId)
                .map(DtoMappers::toProductoViewDTO)
                .flatMap(dto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(dto));
    }

    public Mono<ServerResponse> getProductosDeSucursal(ServerRequest req) {
        String fId = req.pathVariable("franquiciaId");
        String sId = req.pathVariable("sucursalId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoUseCase.getProductosDeSucursal(fId, sId), Producto.class);
    }
}

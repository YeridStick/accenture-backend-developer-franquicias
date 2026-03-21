package co.franquicias.model.sucursal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SucursalRepositoryPort {
    Mono<Sucursal> crear(String franquiciaId, String nombre);
    Mono<Sucursal> findById(String id);
    Flux<Sucursal> listarPorFranquicia(String franquiciaId);
    Mono<String> eliminarPorId(String id);
    Mono<Sucursal> actualizarSucursal(String id, Sucursal cambios);
}

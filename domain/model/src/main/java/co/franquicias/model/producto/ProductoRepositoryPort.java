package co.franquicias.model.producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoRepositoryPort {
    Mono<Producto> crear(String sucursalId, String nombre, int stock);
    Mono<Producto> findById(String id);
    Flux<Producto> listarPorSucursal(String sucursalId);
    Flux<Producto> buscarPorNombreLike(String nombreLike);
    Mono<Producto> actualizarStock(String id, int stock);
    Mono<String> eliminarPorId(String id);
    Mono<Producto> actualizarProducto(String id, Producto cambios);
    Flux<Producto> findAll();
}

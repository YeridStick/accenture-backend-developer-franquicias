package co.franquicias.model.producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoRepositoryPort {
    Mono<Producto> crear(String sucursalId, String nombre, long precio, int stock);
    Mono<Producto> findById(String id);
    Flux<Producto> listarPorSucursal(String sucursalId);
    Flux<Producto> listarPorSucursal(String sucursalId, int page, int size);
    Flux<Producto> buscarPorNombreLike(String nombreLike);
    Flux<Producto> buscarPorNombreLike(String nombreLike, int page, int size);
    Mono<Producto> actualizarStock(String id, int stock);
    Mono<String> eliminarPorId(String id);
    Mono<Producto> actualizarProducto(String id, Producto cambios);
    Flux<Producto> findAll();
    Flux<Producto> findAll(int page, int size);
    Flux<Producto> findTopStockProductsByFranquicia(String franquiciaId);
}

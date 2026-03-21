package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.ProductoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoRepository extends ReactiveCrudRepository<ProductoEntity, String>, ReactiveQueryByExampleExecutor<ProductoEntity> {
    Flux<ProductoEntity> findBySucursalId(String sucursalId);
    Mono<Boolean> existsBySucursalIdAndNombre(String sucursalId, String nombre);
    Flux<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
}

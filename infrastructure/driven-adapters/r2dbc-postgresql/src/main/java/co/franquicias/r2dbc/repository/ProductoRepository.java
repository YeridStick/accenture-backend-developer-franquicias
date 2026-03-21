package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.ProductoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoRepository extends ReactiveCrudRepository<ProductoEntity, String>, ReactiveQueryByExampleExecutor<ProductoEntity> {
    Flux<ProductoEntity> findAllBy(Pageable pageable);
    Flux<ProductoEntity> findBySucursalId(String sucursalId, Pageable pageable);
    Flux<ProductoEntity> findBySucursalId(String sucursalId);
    Mono<Boolean> existsBySucursalIdAndNombre(String sucursalId, String nombre);
    Flux<ProductoEntity> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Flux<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);

    @Query(
        "SELECT p.* FROM productos p " +
        "JOIN sucursales s ON p.sucursal_id = s.id " +
        "WHERE s.franquicia_id = :franquiciaId " +
        "AND p.stock = (SELECT MAX(p2.stock) FROM productos p2 WHERE p2.sucursal_id = s.id)"
    )
    Flux<ProductoEntity> findTopStockProductsByFranquicia(String franquiciaId);
}

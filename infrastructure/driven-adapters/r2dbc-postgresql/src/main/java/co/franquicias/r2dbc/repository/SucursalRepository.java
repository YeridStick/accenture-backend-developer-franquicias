package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.SucursalEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SucursalRepository extends ReactiveCrudRepository<SucursalEntity, String>, ReactiveQueryByExampleExecutor<SucursalEntity> {
    Flux<SucursalEntity> findByFranquiciaId(String franquiciaId);
    Mono<Boolean> existsByFranquiciaIdAndNombre(String franquiciaId, String nombre);
}

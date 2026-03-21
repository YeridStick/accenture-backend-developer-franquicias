package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.FranquiciaEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface FranquiciaRepository extends ReactiveCrudRepository<FranquiciaEntity, String>, ReactiveQueryByExampleExecutor<FranquiciaEntity> {
    Mono<Boolean> existsByNombre(String nombre);
    Mono<FranquiciaEntity> findByNombre(String nombre);
}

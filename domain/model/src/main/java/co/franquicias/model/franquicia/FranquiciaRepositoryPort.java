package co.franquicias.model.franquicia;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranquiciaRepositoryPort {
    Mono<Franquicia> crearFranquicia(String nombre);
    Mono<Franquicia> findById(String id);
    Mono<Franquicia> findByNombre(String nombre);
    Flux<Franquicia> findAll();
    Mono<String> eliminarPorId(String id);
    Mono<Franquicia> actualizarFranquicia(String franquiciaId, Franquicia cambios);
}

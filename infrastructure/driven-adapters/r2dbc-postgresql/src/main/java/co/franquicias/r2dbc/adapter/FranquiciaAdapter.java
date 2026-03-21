package co.franquicias.r2dbc.adapter;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.franquicia.Franquicia;
import co.franquicias.model.franquicia.FranquiciaRepositoryPort;
import co.franquicias.r2dbc.entity.FranquiciaEntity;
import co.franquicias.r2dbc.helper.ReactiveAdapterOperations;
import co.franquicias.r2dbc.repository.FranquiciaRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public class FranquiciaAdapter extends ReactiveAdapterOperations<Franquicia, FranquiciaEntity, String, FranquiciaRepository>
        implements FranquiciaRepositoryPort {

    public FranquiciaAdapter(FranquiciaRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franquicia.class));
    }

    @Override
    public Mono<Franquicia> crearFranquicia(String nombre) {
        var data = FranquiciaEntity.builder()
                .id(UUID.randomUUID().toString())
                .nombre(nombre)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return repository.existsByNombre(nombre)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new ConflictException("La franquicia con nombre '" + nombre + "' ya existe"))
                        : repository.save(data))
                .map(this::toEntity);
    }

    public Mono<Franquicia> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(this::toEntity);
    }

    public Mono<Franquicia> findById(String id) {
        return repository.findById(id).map(this::toEntity);
    }

    public Mono<String> eliminarPorId(String id) {
        return repository.deleteById(id).thenReturn("Franquicia eliminada correctamente");
    }

    public Mono<Franquicia> actualizarFranquicia(String franquiciaId, Franquicia cambios) {
        cambios.setUpdatedAt(Instant.now());
        return mergeNonNullAndSave(franquiciaId, cambios);
    }
}

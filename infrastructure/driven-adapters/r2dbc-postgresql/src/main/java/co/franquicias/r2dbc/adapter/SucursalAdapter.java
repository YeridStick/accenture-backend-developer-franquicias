package co.franquicias.r2dbc.adapter;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.error.NotFoundException;
import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import co.franquicias.r2dbc.entity.SucursalEntity;
import co.franquicias.r2dbc.helper.ReactiveAdapterOperations;
import co.franquicias.r2dbc.repository.SucursalRepository;
import co.franquicias.r2dbc.repository.FranquiciaRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public class SucursalAdapter extends ReactiveAdapterOperations<Sucursal, SucursalEntity, String, SucursalRepository> implements SucursalRepositoryPort {

    private final FranquiciaRepository franquiciaRepository;

    public SucursalAdapter(SucursalRepository repository, ObjectMapper mapper, FranquiciaRepository franquiciaRepository) {
        super(repository, mapper, d -> mapper.map(d, Sucursal.class));
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Sucursal> crear(String franquiciaId, String nombre) {
        var now = Instant.now();
        var data = SucursalEntity.builder()
                .id(UUID.randomUUID().toString())
                .franquiciaId(franquiciaId)
                .nombre(nombre)
                .createdAt(now)
                .updatedAt(now)
                .isNewRecord(true)
                .build();

        return franquiciaRepository.existsById(franquiciaId)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? repository.existsByFranquiciaIdAndNombre(franquiciaId, nombre)
                        .flatMap(dup -> Boolean.TRUE.equals(dup)
                                ? Mono.error(new ConflictException("La sucursal '" + nombre + "' ya existe en esta franquicia"))
                                : repository.save(data))
                        : Mono.error(new NotFoundException("La franquicia no existe: " + franquiciaId))
                )
                .map(this::toEntity);
    }

    @Override
    public Flux<Sucursal> listarPorFranquicia(String franquiciaId) {
        return repository.findByFranquiciaId(franquiciaId).map(this::toEntity);
    }

    @Override
    public Mono<String> eliminarPorId(String id) {
        return repository.deleteById(id).thenReturn("Sucursal eliminada correctamente");
    }

    @Override
    public Mono<Sucursal> actualizarSucursal(String id, Sucursal cambios) {
        cambios.setUpdatedAt(Instant.now());
        return mergeNonNullAndSave(id, cambios);
    }
}

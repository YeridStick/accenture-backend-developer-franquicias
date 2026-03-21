package co.franquicias.r2dbc.adapter;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.franquicia.Franquicia;
import co.franquicias.r2dbc.entity.FranquiciaEntity;
import co.franquicias.r2dbc.repository.FranquiciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranquiciaAdapterTest {

    @Mock
    private FranquiciaRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private FranquiciaAdapter adapter;

    @BeforeEach
    void setUp() {
        // FranquiciaAdapter uses super constructor which uses mapper
        // We might need to mock mapper behavior if it's called in constructor or toEntity
    }

    @Test
    void crearFranquicia_WhenNotExists_ShouldSave() {
        String name = "Franquicia 1";
        FranquiciaEntity entity = FranquiciaEntity.builder()
                .id(UUID.randomUUID().toString())
                .nombre(name)
                .build();
        Franquicia domain = new Franquicia();
        domain.setId(entity.getId());
        domain.setNombre(name);

        when(repository.existsByNombre(name)).thenReturn(Mono.just(false));
        when(repository.save(any(FranquiciaEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Franquicia.class)).thenReturn(domain);

        StepVerifier.create(adapter.crearFranquicia(name))
                .expectNextMatches(f -> f.getNombre().equals(name))
                .verifyComplete();
    }

    @Test
    void crearFranquicia_WhenExists_ShouldReturnConflict() {
        String name = "Existing";
        when(repository.existsByNombre(name)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.crearFranquicia(name))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void findByNombre_ShouldReturnFranquicia() {
        String name = "F1";
        FranquiciaEntity entity = FranquiciaEntity.builder()
                .nombre(name)
                .build();
        Franquicia domain = new Franquicia();
        domain.setNombre(name);

        when(repository.findByNombre(name)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Franquicia.class)).thenReturn(domain);

        StepVerifier.create(adapter.findByNombre(name))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void eliminarPorId_ShouldReturnSuccessMessage() {
        String id = "1";
        when(repository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.eliminarPorId(id))
                .expectNext("Franquicia eliminada correctamente")
                .verifyComplete();
    }
}

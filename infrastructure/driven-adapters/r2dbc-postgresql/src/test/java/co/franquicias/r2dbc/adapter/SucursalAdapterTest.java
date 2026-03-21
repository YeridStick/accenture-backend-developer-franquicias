package co.franquicias.r2dbc.adapter;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.error.NotFoundException;
import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.r2dbc.entity.SucursalEntity;
import co.franquicias.r2dbc.repository.FranquiciaRepository;
import co.franquicias.r2dbc.repository.SucursalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SucursalAdapterTest {

    @Mock
    private SucursalRepository repository;

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private SucursalAdapter adapter;

    @Test
    void crear_WhenFranquiciaExistsAndNoDuplicate_ShouldSave() {
        String fId = "f1";
        String name = "S1";
        SucursalEntity entity = SucursalEntity.builder().id("s1").nombre(name).franquiciaId(fId).build();
        Sucursal domain = new Sucursal();
        domain.setId("s1");
        domain.setNombre(name);

        when(franquiciaRepository.existsById(fId)).thenReturn(Mono.just(true));
        when(repository.existsByFranquiciaIdAndNombre(fId, name)).thenReturn(Mono.just(false));
        when(repository.save(any(SucursalEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Sucursal.class)).thenReturn(domain);

        StepVerifier.create(adapter.crear(fId, name))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void crear_WhenFranquiciaNotFound_ShouldReturnError() {
        String fId = "missing";
        when(franquiciaRepository.existsById(fId)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.crear(fId, "S1"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void crear_WhenDuplicateSucursal_ShouldReturnConflict() {
        String fId = "f1";
        String name = "Existing";
        when(franquiciaRepository.existsById(fId)).thenReturn(Mono.just(true));
        when(repository.existsByFranquiciaIdAndNombre(fId, name)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.crear(fId, name))
                .expectError(ConflictException.class)
                .verify();
    }
}

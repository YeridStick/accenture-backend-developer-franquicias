package co.franquicias.usecase.franquicia;

import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SucursalUseCaseTest {

    @Mock
    private SucursalRepositoryPort repository;

    private SucursalUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SucursalUseCase(repository);
    }

    @Test
    void agregarSucursal() {
        String fId = "f1";
        String name = "S1";
        Sucursal s = Sucursal.builder().id("s1").franquiciaId(fId).nombre(name).build();

        when(repository.crear(fId, name)).thenReturn(Mono.just(s));

        StepVerifier.create(useCase.agregarSucursal(fId, name))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void obtenerSucursalPorId() {
        String id = "s1";
        Sucursal s = Sucursal.builder().id(id).nombre("S1").build();

        when(repository.findById(id)).thenReturn(Mono.just(s));

        StepVerifier.create(useCase.obtenerSucursalPorId(id))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void obtenerSucursalPorFranquiciaId() {
        String fId = "f1";
        Sucursal s = Sucursal.builder().id("s1").franquiciaId(fId).nombre("S1").build();

        when(repository.listarPorFranquicia(fId)).thenReturn(Flux.just(s));

        StepVerifier.create(useCase.obtenerSucursalPorFranquiciaId(fId))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void eliminarSucursalPorId() {
        String id = "s1";
        when(repository.eliminarPorId(id)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(useCase.eliminarSucursalPorId(id))
                .expectNext("Deleted")
                .verifyComplete();
    }

    @Test
    void actualizarSucursal() {
        String id = "s1";
        Sucursal cambios = Sucursal.builder().nombre("Nuevo Nombre").build();
        Sucursal result = Sucursal.builder().id(id).nombre("Nuevo Nombre").build();

        when(repository.actualizarSucursal(id, cambios)).thenReturn(Mono.just(result));

        StepVerifier.create(useCase.actualizarSucursal(id, cambios))
                .expectNext(result)
                .verifyComplete();
    }
}

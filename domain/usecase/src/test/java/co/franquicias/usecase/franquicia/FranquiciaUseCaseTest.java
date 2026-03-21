package co.franquicias.usecase.franquicia;

import co.franquicias.model.franquicia.Franquicia;
import co.franquicias.model.franquicia.FranquiciaRepositoryPort;
import co.franquicias.model.producto.Producto;
import co.franquicias.model.producto.ProductoRepositoryPort;
import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranquiciaUseCaseTest {

    @Mock FranquiciaRepositoryPort franquiciaRepo;
    @Mock SucursalRepositoryPort sucursalRepo;
    @Mock ProductoRepositoryPort productoRepo;

    FranquiciaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FranquiciaUseCase(franquiciaRepo, sucursalRepo, productoRepo);
    }

    private Franquicia franq(String id, String nombre) {
        return Franquicia.builder().id(id).nombre(nombre).build();
    }
    private Sucursal suc(String id, String fid, String nombre) {
        return Sucursal.builder().id(id).franquiciaId(fid).nombre(nombre).build();
    }
    private Producto prod(String id, String sid, String nombre, int stock) {
        return Producto.builder().id(id).sucursalId(sid).nombre(nombre).stock(stock).build();
    }

    @Test
    @DisplayName("crearFranquicia: delega al repo")
    void crearFranquicia_ok() {
        when(franquiciaRepo.crearFranquicia("F1")).thenReturn(Mono.just(franq("f1", "F1")));
        StepVerifier.create(useCase.crearFranquicia("F1"))
                .expectNextMatches(f -> f.getNombre().equals("F1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("obtenerPorId: hidrata sucursales y productos")
    void obtenerPorId_hydration() {
        String fId = "f1";
        String sId = "s1";
        Franquicia f = franq(fId, "F1");
        Sucursal s = suc(sId, fId, "S1");
        Producto p = prod("p1", sId, "P1", 10);

        when(franquiciaRepo.findById(fId)).thenReturn(Mono.just(f));
        when(sucursalRepo.listarPorFranquicia(fId)).thenReturn(Flux.just(s));
        when(productoRepo.listarPorSucursal(sId)).thenReturn(Flux.just(p));

        StepVerifier.create(useCase.obtenerPorId(fId))
                .expectNextMatches(result -> {
                    return result.getSucursales().size() == 1 &&
                           result.getSucursales().get(0).getProductos().size() == 1 &&
                           result.getSucursales().get(0).getProductos().get(0).getNombre().equals("P1");
                })
                .verifyComplete();
    }

    @Test
    void eliminarFranquiciaPorId() {
        when(franquiciaRepo.eliminarPorId("f1")).thenReturn(Mono.just("ok"));
        StepVerifier.create(useCase.eliminarFranquiciaPorId("f1"))
                .expectNext("ok").verifyComplete();
    }
}

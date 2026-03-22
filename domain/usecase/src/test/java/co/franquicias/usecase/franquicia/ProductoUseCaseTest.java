package co.franquicias.usecase.franquicia;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.error.NotFoundException;
import co.franquicias.model.franquicia.FranquiciaRepositoryPort;
import co.franquicias.model.producto.Producto;
import co.franquicias.model.producto.ProductoRepositoryPort;
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

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseTest {

    @Mock
    private ProductoRepositoryPort productoRepository;
    @Mock
    private SucursalRepositoryPort sucursalRepository;
    @Mock
    private FranquiciaRepositoryPort franquiciaRepository;

    private ProductoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProductoUseCase(productoRepository, sucursalRepository, franquiciaRepository);
    }

    @Test
    void agregarProducto_Success() {
        String fId = "f1";
        String sId = "s1";
        String pName = "P1";
        
        Sucursal s = Sucursal.builder().id(sId).franquiciaId(fId).build();
        Producto p = Producto.builder().id("p1").nombre(pName).build();

        when(sucursalRepository.findById(sId)).thenReturn(Mono.just(s));
        when(productoRepository.crear(sId, pName, 100L, 10)).thenReturn(Mono.just(p));

        StepVerifier.create(useCase.agregarProducto(fId, sId, pName, 100L, 10))
                .expectNext(p)
                .verifyComplete();
    }

    @Test
    void agregarProducto_Conflict_WrongFranquicia() {
        String fId = "f1";
        String sId = "s1";
        Sucursal s = Sucursal.builder().id(sId).franquiciaId("f2").build(); // Wrong franchise

        when(sucursalRepository.findById(sId)).thenReturn(Mono.just(s));

        StepVerifier.create(useCase.agregarProducto(fId, sId, "P1", 100L, 10))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void actualizarStock_Success() {
        String fId = "f1";
        String sId = "s1";
        String pId = "p1";
        
        Sucursal s = Sucursal.builder().id(sId).franquiciaId(fId).build();
        Producto p = Producto.builder().id(pId).stock(20).build();

        when(sucursalRepository.findById(sId)).thenReturn(Mono.just(s));
        when(productoRepository.actualizarStock(pId, 20)).thenReturn(Mono.just(p));

        StepVerifier.create(useCase.actualizarStock(fId, sId, pId, 20))
                .expectNext(p)
                .verifyComplete();
    }

    @Test
    void maxStockPorSucursal() {
        String fId = "f1";
        Producto p1 = Producto.builder().id("p1").sucursalId("s1").nombre("P1").stock(50).build();
        Sucursal s1 = Sucursal.builder().id("s1").nombre("S1").build();

        when(productoRepository.findTopStockProductsByFranquicia(fId)).thenReturn(Flux.just(p1));
        when(sucursalRepository.findById("s1")).thenReturn(Mono.just(s1));

        StepVerifier.create(useCase.maxStockPorSucursal(fId))
                .expectNextMatches(map -> 
                    map.get("sucursalNombre").equals("S1") && 
                    map.get("productoNombre").equals("P1") &&
                    map.get("stock").equals(50)
                )
                .verifyComplete();
    }

    @Test
    void getProductoGlobal_Success() {
        String pId = "p1";
        String sId = "s1";
        Producto p = Producto.builder().id(pId).sucursalId(sId).nombre("P1").stock(10).build();
        Sucursal s = Sucursal.builder().id(sId).nombre("S1").franquiciaId("f1").build();
        co.franquicias.model.franquicia.Franquicia f = co.franquicias.model.franquicia.Franquicia.builder().id("f1").nombre("F1").build();

        when(productoRepository.findById(pId)).thenReturn(Mono.just(p));
        when(sucursalRepository.findById(sId)).thenReturn(Mono.just(s));
        when(franquiciaRepository.findById("f1")).thenReturn(Mono.just(f));

        StepVerifier.create(useCase.getProductoGlobal(pId))
                .expectNextMatches(map -> 
                    map.get("productoNombre").equals("P1") && 
                    map.get("sucursalNombre").equals("S1") &&
                    map.get("franquiciaId").equals("f1")
                )
                .verifyComplete();
    }

    @Test
    void getProductosDeSucursal_Conflict() {
        String fId = "f1";
        String sId = "s1";
        Sucursal s = Sucursal.builder().id(sId).franquiciaId("fOther").build();

        when(sucursalRepository.findById(sId)).thenReturn(Mono.just(s));

        StepVerifier.create(useCase.getProductosDeSucursal(fId, sId))
                .expectError(ConflictException.class)
                .verify();
    }
}

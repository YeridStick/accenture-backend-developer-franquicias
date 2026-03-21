package co.franquicias.r2dbc.adapter;

import co.franquicias.model.producto.Producto;
import co.franquicias.r2dbc.entity.ProductoEntity;
import co.franquicias.r2dbc.repository.ProductoRepository;
import co.franquicias.r2dbc.repository.SucursalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoAdapterTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ProductoAdapter adapter;

    @Test
    void crear_WhenSucursalExistsAndNoDuplicate_ShouldSave() {
        String sId = "s1";
        String name = "P1";
        ProductoEntity entity = ProductoEntity.builder().id("p1").nombre(name).sucursalId(sId).build();
        Producto domain = new Producto();
        domain.setId("p1");
        domain.setNombre(name);

        when(sucursalRepository.existsById(sId)).thenReturn(Mono.just(true));
        when(repository.existsBySucursalIdAndNombre(sId, name)).thenReturn(Mono.just(false));
        when(repository.save(any(ProductoEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Producto.class)).thenReturn(domain);

        StepVerifier.create(adapter.crear(sId, name, 10))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void actualizarStock_WhenExists_ShouldUpdate() {
        String pId = "p1";
        ProductoEntity entity = ProductoEntity.builder().id(pId).stock(5).build();
        Producto domain = new Producto();
        domain.setId(pId);
        domain.setStock(10);

        when(repository.findById(pId)).thenReturn(Mono.just(entity));
        when(repository.save(any(ProductoEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(), eq(Producto.class))).thenReturn(domain);

        StepVerifier.create(adapter.actualizarStock(pId, 10))
                .expectNextMatches(p -> p.getStock() == 10)
                .verifyComplete();
    }

    @Test
    void findTopStockProductsByFranquicia_ShouldDelegateToRepository() {
        String fId = "f1";
        ProductoEntity entity = new ProductoEntity();
        Producto domain = new Producto();

        when(repository.findTopStockProductsByFranquicia(fId)).thenReturn(Flux.just(entity));
        when(mapper.map(entity, Producto.class)).thenReturn(domain);

        StepVerifier.create(adapter.findTopStockProductsByFranquicia(fId))
                .expectNext(domain)
                .verifyComplete();
    }
}

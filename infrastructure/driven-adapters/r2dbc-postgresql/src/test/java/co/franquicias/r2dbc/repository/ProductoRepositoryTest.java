package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.FranquiciaEntity;
import co.franquicias.r2dbc.entity.ProductoEntity;
import co.franquicias.r2dbc.entity.SucursalEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

@DataR2dbcTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private FranquiciaRepository franquiciaRepository;

    private Mono<SucursalEntity> setupData() {
        return franquiciaRepository.save(FranquiciaEntity.builder()
                        .nombre("F-Prod-" + UUID.randomUUID())
                        .build())
                .flatMap(f -> sucursalRepository.save(SucursalEntity.builder()
                        .franquiciaId(f.getId())
                        .nombre("S-Prod-" + UUID.randomUUID())
                        .build()));
    }

    @Test
    void saveAndFind() {
        setupData()
                .flatMap(sucursal -> repository.save(ProductoEntity.builder()
                        .sucursalId(sucursal.getId())
                        .nombre("P1")
                        .precio(100L)
                        .stock(50)
                        .build()))
                .flatMap(prod -> repository.findBySucursalId(prod.getSucursalId()).next())
                .as(StepVerifier::create)
                .expectNextMatches(prod -> prod.getNombre().equals("P1"))
                .verifyComplete();
    }

    @Test
    void findTopStockProductsByFranquicia() {
        Mono<FranquiciaEntity> complexSetup = franquiciaRepository.save(FranquiciaEntity.builder()
                        .nombre("F-Complex-" + UUID.randomUUID())
                        .build())
                .flatMap(f -> sucursalRepository.save(SucursalEntity.builder()
                        .franquiciaId(f.getId())
                        .nombre("S1")
                        .build())
                        .zipWith(sucursalRepository.save(SucursalEntity.builder()
                                .franquiciaId(f.getId())
                                .nombre("S2")
                                .build()))
                        .flatMap(tuple -> {
                            String sid1 = tuple.getT1().getId();
                            String sid2 = tuple.getT2().getId();
                            return repository.save(ProductoEntity.builder().sucursalId(sid1).nombre("P1-S1").stock(10).build())
                                     .then(repository.save(ProductoEntity.builder().sucursalId(sid1).nombre("P1-MAX").stock(50).build()))
                                     .then(repository.save(ProductoEntity.builder().sucursalId(sid2).nombre("P2-S2").stock(20).build()))
                                     .then(repository.save(ProductoEntity.builder().sucursalId(sid2).nombre("P2-MAX").stock(80).build()));
                        })
                        .thenReturn(f)
                );

        complexSetup.flatMapMany(f -> repository.findTopStockProductsByFranquicia(f.getId()))
                .as(StepVerifier::create)
                .expectNextMatches(p -> p.getNombre().startsWith("P2-MAX"))
                .expectNextMatches(p -> p.getNombre().startsWith("P1-MAX"))
                .verifyComplete();
    }
}

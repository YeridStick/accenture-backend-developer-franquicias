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

    private Mono<Void> setupData(String fId, String sId) {
        return franquiciaRepository.save(FranquiciaEntity.builder()
                        .id(fId)
                        .nombre("F-Prod-" + fId)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build())
                .then(sucursalRepository.save(SucursalEntity.builder()
                        .id(sId)
                        .franquiciaId(fId)
                        .nombre("S-Prod-" + sId)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()))
                .then();
    }

    @Test
    void saveAndFind() {
        String fId = UUID.randomUUID().toString();
        String sId = UUID.randomUUID().toString();
        String pId = UUID.randomUUID().toString();

        setupData(fId, sId)
                .then(repository.save(ProductoEntity.builder()
                        .id(pId)
                        .sucursalId(sId)
                        .nombre("P1")
                        .precio(100)
                        .stock(50)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.findBySucursalId(sId)
                .as(StepVerifier::create)
                .expectNextMatches(prod -> prod.getNombre().equals("P1"))
                .verifyComplete();
    }

    @Test
    void findTopStockProductsByFranquicia() {
        String fId = UUID.randomUUID().toString();
        String sId1 = UUID.randomUUID().toString();
        String sId2 = UUID.randomUUID().toString();

        Mono<Void> complexSetup = franquiciaRepository.save(FranquiciaEntity.builder()
                        .id(fId)
                        .nombre("F-Complex")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build())
                .then(sucursalRepository.save(SucursalEntity.builder()
                        .id(sId1)
                        .franquiciaId(fId)
                        .nombre("S1")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()))
                .then(sucursalRepository.save(SucursalEntity.builder()
                        .id(sId2)
                        .franquiciaId(fId)
                        .nombre("S2")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()))
                .then(repository.save(ProductoEntity.builder().id(UUID.randomUUID().toString()).sucursalId(sId1).nombre("P1-S1").stock(10).createdAt(Instant.now()).updatedAt(Instant.now()).build()))
                .then(repository.save(ProductoEntity.builder().id(UUID.randomUUID().toString()).sucursalId(sId1).nombre("P1-MAX").stock(50).createdAt(Instant.now()).updatedAt(Instant.now()).build()))
                .then(repository.save(ProductoEntity.builder().id(UUID.randomUUID().toString()).sucursalId(sId2).nombre("P2-S2").stock(20).createdAt(Instant.now()).updatedAt(Instant.now()).build()))
                .then(repository.save(ProductoEntity.builder().id(UUID.randomUUID().toString()).sucursalId(sId2).nombre("P2-MAX").stock(80).createdAt(Instant.now()).updatedAt(Instant.now()).build()))
                .then();

        complexSetup.thenMany(repository.findTopStockProductsByFranquicia(fId))
                .as(StepVerifier::create)
                .expectNextMatches(p -> p.getNombre().startsWith("P1-MAX"))
                .expectNextMatches(p -> p.getNombre().startsWith("P2-MAX"))
                .verifyComplete();
    }
}

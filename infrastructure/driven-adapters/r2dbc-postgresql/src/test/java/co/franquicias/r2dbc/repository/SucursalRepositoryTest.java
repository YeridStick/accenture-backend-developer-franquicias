package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.FranquiciaEntity;
import co.franquicias.r2dbc.entity.SucursalEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

@DataR2dbcTest
class SucursalRepositoryTest {

    @Autowired
    private SucursalRepository repository;

    @Autowired
    private FranquiciaRepository franquiciaRepository;

    @Test
    void saveAndFind() {
        String fId = UUID.randomUUID().toString();
        String sId = UUID.randomUUID().toString();
        String fName = "F-" + fId;
        String sName = "S-" + sId;

        franquiciaRepository.save(FranquiciaEntity.builder()
                        .id(fId)
                        .nombre(fName)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build())
                .then(repository.save(SucursalEntity.builder()
                        .id(sId)
                        .franquiciaId(fId)
                        .nombre(sName)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()))
                .thenMany(repository.findByFranquiciaId(fId))
                .as(StepVerifier::create)
                .expectNextMatches(suc -> suc.getNombre().equals(sName))
                .verifyComplete();

        repository.existsByFranquiciaIdAndNombre(fId, sName)
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }
}

package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.FranquiciaEntity;
import co.franquicias.r2dbc.entity.SucursalEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest
class SucursalRepositoryTest {

    @Autowired
    private SucursalRepository repository;

    @Autowired
    private FranquiciaRepository franquiciaRepository;

    @Test
    void saveAndFind() {
        String fName = "F-Test-" + UUID.randomUUID();
        String sName = "S-Test-" + UUID.randomUUID();

        franquiciaRepository.save(FranquiciaEntity.builder()
                        .nombre(fName)
                        .build())
                .flatMap(f -> repository.save(SucursalEntity.builder()
                        .franquiciaId(f.getId())
                        .nombre(sName)
                        .build()))
                .flatMapMany(s -> repository.findByFranquiciaId(s.getFranquiciaId()))
                .as(StepVerifier::create)
                .expectNextMatches(suc -> {
                    assertNotNull(suc.getId());
                    return suc.getNombre().equals(sName);
                })
                .verifyComplete();

        repository.findAll()
                .filter(s -> s.getNombre().equals(sName))
                .flatMap(s -> repository.existsByFranquiciaIdAndNombre(s.getFranquiciaId(), s.getNombre()))
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }
}

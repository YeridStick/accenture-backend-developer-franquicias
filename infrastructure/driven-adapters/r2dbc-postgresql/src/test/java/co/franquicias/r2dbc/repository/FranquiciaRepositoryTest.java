package co.franquicias.r2dbc.repository;

import co.franquicias.r2dbc.entity.FranquiciaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class FranquiciaRepositoryTest {

    @Autowired
    private FranquiciaRepository repository;

    @Test
    void saveAndFind() {
        FranquiciaEntity entity = FranquiciaEntity.builder()
                .nombre("Test Franquicia")
                .build();

        repository.save(entity)
                .as(StepVerifier::create)
                .expectNextMatches(f -> {
                    assertNotNull(f.getId());
                    return f.getNombre().equals("Test Franquicia");
                })
                .verifyComplete();

        repository.findByNombre("Test Franquicia")
                .as(StepVerifier::create)
                .expectNextMatches(f -> f.getNombre().equals("Test Franquicia"))
                .verifyComplete();

        repository.existsByNombre("Test Franquicia")
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }
}

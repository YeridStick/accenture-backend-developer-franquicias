package co.franquicias.config;

import co.franquicias.model.franquicia.FranquiciaRepositoryPort;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import co.franquicias.model.producto.ProductoRepositoryPort;
import co.franquicias.usecase.franquicia.FranquiciaUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UseCasesConfigTest {

    @Test
    void componentScan_annotation_is_correct() {
        ComponentScan scan = UseCasesConfig.class.getAnnotation(ComponentScan.class);
        assertNotNull(scan, "@ComponentScan no está presente");
        assertArrayEquals(new String[]{"co.franquicias.usecase"}, scan.basePackages());
        assertEquals("^.+UseCase$", scan.includeFilters()[0].pattern()[0]);
    }

    @Test
    void context_registers_real_UseCase_when_dependencies_provided() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(UseCasesConfig.class, SupportConfig.class);
            ctx.refresh();

            FranquiciaUseCase useCase = ctx.getBean(FranquiciaUseCase.class);
            assertNotNull(useCase);
        }
    }

    @Configuration
    static class SupportConfig {
        @Bean FranquiciaRepositoryPort f() { return Mockito.mock(FranquiciaRepositoryPort.class); }
        @Bean SucursalRepositoryPort s() { return Mockito.mock(SucursalRepositoryPort.class); }
        @Bean ProductoRepositoryPort p() { return Mockito.mock(ProductoRepositoryPort.class); }
    }
}

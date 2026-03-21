package co.franquicias.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Franquicias")
                        .version("1.0.0")
                        .description("Documentación Prueba tecnica de la API para la gestión de franquicias, sucursales y productos.")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new Info().getLicense() != null ? new Info().getLicense() : new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}

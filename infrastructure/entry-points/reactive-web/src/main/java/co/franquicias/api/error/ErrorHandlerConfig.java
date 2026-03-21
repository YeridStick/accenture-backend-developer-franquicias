package co.franquicias.api.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorHandlerConfig {
    @Bean
    public ErrorWebExceptionHandler globalErrorHandler(ObjectMapper objectMapper) {
        return new GlobalErrorHandler(objectMapper);
    }
}

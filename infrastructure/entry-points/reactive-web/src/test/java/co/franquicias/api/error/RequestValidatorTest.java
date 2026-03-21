package co.franquicias.api.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private RequestValidator requestValidator;

    @Test
    void validate_WhenNoViolations_ShouldReturnMonoWithObject() {
        String testObject = "Test";
        when(validator.validate(testObject)).thenReturn(Set.of());

        StepVerifier.create(requestValidator.validate(testObject))
                .expectNext(testObject)
                .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    void validate_WhenViolationsExist_ShouldReturnError() {
        String testObject = "Test";
        ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        when(validator.validate(testObject)).thenReturn(Set.of(violation));

        StepVerifier.create(requestValidator.validate(testObject))
                .expectError(ConstraintViolationException.class)
                .verify();
    }
}
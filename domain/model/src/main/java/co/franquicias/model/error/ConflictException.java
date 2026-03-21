package co.franquicias.model.error;

public class ConflictException extends BusinessException {
    public ConflictException(String message) {
        super(message);
    }
}

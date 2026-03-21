package co.franquicias.model.error;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }
}

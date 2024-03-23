package PocketTrack.Serverapp.Exceptions;

/**
 * Only used if there is field not valid in JSON
 */
public class RequiredFieldNotValidException extends RuntimeException {
    public RequiredFieldNotValidException(String message) {
        super(message);
    }
}

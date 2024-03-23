package PocketTrack.Serverapp.Exceptions;

/**
 * Only used if there is field is missing in JSON
 */
public class RequiredFieldIsMissingException extends RuntimeException {
    public RequiredFieldIsMissingException(String message) {
        super(message);
    }
}

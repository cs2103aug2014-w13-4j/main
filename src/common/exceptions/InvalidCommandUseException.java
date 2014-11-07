package common.exceptions;

/**
 * Thrown if the command is used inappropriately by the user
 *
 */
public class InvalidCommandUseException extends Exception {
    public InvalidCommandUseException(String message) {
        super(message);
    }
}
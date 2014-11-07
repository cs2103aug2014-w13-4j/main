package common.exceptions;

/**
 * Thrown if the user input is invalid
 *
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

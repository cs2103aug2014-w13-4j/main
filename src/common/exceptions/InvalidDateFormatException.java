package common.exceptions;

/**
 * Thrown if the date format given by the user cannot be parsed by the date
 * parser
 *
 */
public class InvalidDateFormatException extends Exception {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}

package exceptions;

public class InvalidCommandUseException extends Exception {
    public InvalidCommandUseException(String message) {
        super(message);
    }
}
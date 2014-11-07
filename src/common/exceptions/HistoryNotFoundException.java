package common.exceptions;

/**
 * Thrown if there is no more action to be undone
 *
 */
public class HistoryNotFoundException extends Exception {
    public HistoryNotFoundException(String message) {
        super(message);
    }

}

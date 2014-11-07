package common.exceptions;

/**
 * Thrown if the time interval of the task overlaps with an existing one
 *
 */
public class TimeIntervalOverlapException extends Exception {
    public TimeIntervalOverlapException(String message) {
        super(message);
    }
}

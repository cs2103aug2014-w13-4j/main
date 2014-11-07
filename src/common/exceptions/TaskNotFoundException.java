package common.exceptions;

/**
 *
 * This exception is thrown if the task cannot be found with the given id
 *
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
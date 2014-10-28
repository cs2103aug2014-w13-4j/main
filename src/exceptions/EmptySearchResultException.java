package exceptions;

public class EmptySearchResultException extends Exception {
    public EmptySearchResultException(String message) {
        super(message);
    }
}
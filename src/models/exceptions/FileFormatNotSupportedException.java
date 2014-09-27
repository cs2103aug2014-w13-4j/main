package models.exceptions;

public class FileFormatNotSupportedException extends Exception {
    public FileFormatNotSupportedException(String message) {
        super(message);
    }
}
package ua.edu.ukma.user.utils.exceptions;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException() {
        super("User with such email already exists");
    }
}

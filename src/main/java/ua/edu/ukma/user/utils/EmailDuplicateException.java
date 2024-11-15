package ua.edu.ukma.user.utils;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException() {
        super("User with such email already exists");
    }
}
